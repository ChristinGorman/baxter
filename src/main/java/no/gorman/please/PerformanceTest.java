package no.gorman.please;

import no.gorman.database.DB;
import no.gorman.database.DBFunctions;
import no.gorman.database.DatabaseColumns;
import no.gorman.database.Where;
import no.gorman.please.common.Child;
import no.gorman.please.common.GrownUp;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.*;

import static no.gorman.database.DatabaseColumns.email;

public class PerformanceTest {

    static Map<String, List<Long>> times = new ConcurrentHashMap<>();
    static CountDownLatch latch;
    private static Connection connection;
    private static DB db;
    public static void main(String[] args) throws Exception {

        DBFunctions.setupConnectionPool("jdbc:postgresql://localhost/bax?useUnicode=true&characterEncoding=utf8", "postgres", "baxter", 3);
        connection = DBFunctions.pool.getConnection();
        times.put("scheduleNames", new Vector<>());
        times.put("event", new Vector<>());
        times.put("children", new Vector<>());
        times.put("poll", new Vector<>());
        times.put("edit", new Vector<>());

        ExecutorService executorService = Executors.newFixedThreadPool(8);
        db = new DB(connection);
        List<GrownUp> grownups = db.select(GrownUp.class);
        latch = new CountDownLatch(grownups.size());
        long timestamp = System.currentTimeMillis();
        for (GrownUp g : grownups) {
            System.out.print(g.getEmail());
            executorService.submit(new Client(g.getEmail(), g.getPassword()));
            System.out.println(" done");
        }
        latch.await(5, TimeUnit.MINUTES);
        System.out.println(System.currentTimeMillis() - timestamp);
        for (Map.Entry<String, List<Long>> time : times.entrySet()) {
            System.out.println(time.getKey());
            if (time.getValue().isEmpty())continue;
            ArrayList<Long> sorted = new ArrayList<>(time.getValue());
            Collections.sort(sorted);
            System.out.println("median: " + sorted.get(sorted.size()/2));
            System.out.println("min: " + sorted.get(0));
            System.out.println("max: " + sorted.get(sorted.size()-1));
        }
    }

    private static class Client implements Runnable {
        final String username;
        final String password;
        final long grownupId;

        public Client(String username, String password) {
            this.username = username;
            this.password = password;
            this.grownupId = db.select(DatabaseColumns.grownup_id, Long.TYPE, new Where(email, " = ", username)).get(0);
        }

        @Override
        public void run() {
            try {
                Thread.currentThread().setName(username);
                DefaultHttpClient httpclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://localhost:5000/login");
                List<NameValuePair> nvps = new ArrayList<>();
                nvps.add(new BasicNameValuePair("email", username));
                nvps.add(new BasicNameValuePair("password", password));
                httpPost.setEntity(new UrlEncodedFormEntity(nvps));
                HttpResponse response2 = httpclient.execute(httpPost);
                try {
                    response2.getStatusLine();
                    HttpEntity entity2 = response2.getEntity();
                    // do something useful with the response body
                    // and ensure it is fully consumed
                    EntityUtils.consume(entity2);
                } finally {
                    httpPost.releaseConnection();
                }
                times.get("scheduleNames").add(postAjaxRequest(httpclient, scheduleNameRequest()));
                times.get("event").add(postAjaxRequest(httpclient, eventRequest()));
                times.get("children").add(postAjaxRequest(httpclient, childRequest()));
                times.get("edit").add(postAjaxRequest(httpclient, editChildRequest()));
                times.get("poll").add(postAjaxRequest(httpclient, pollRequest()));

            } catch (Exception e) {
                throw new RuntimeException();
            } finally {
                latch.countDown();
                Thread.currentThread().setName(username + " done");
            }
        }

        private long postAjaxRequest(DefaultHttpClient httpclient, HttpPost post ) throws IOException {
            long time = System.currentTimeMillis();
            HttpResponse response = httpclient.execute(post);
            try {
                response.getStatusLine();
                HttpEntity entity2 = response.getEntity();
                // do something useful with the response body
                // and ensure it is fully consumed
                EntityUtils.consume(entity2);
            } finally {
                post.releaseConnection();
            }
            return System.currentTimeMillis() - time;
        }

        private HttpPost scheduleNameRequest() throws UnsupportedEncodingException {
            HttpPost httpPost;List<NameValuePair> nvps;
            httpPost = new HttpPost("http://localhost:5000/overview");
            nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("action", "scheduleNames"));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps);
            httpPost.setEntity(entity);
            return httpPost;
        }

        private HttpPost eventRequest() throws UnsupportedEncodingException {
            HttpPost httpPost;List<NameValuePair> nvps;
            httpPost = new HttpPost("http://localhost:5000/timeline");
            nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("action", "get"));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps);
            httpPost.setEntity(entity);
            return httpPost;
        }

        private HttpPost childRequest() throws UnsupportedEncodingException {
            HttpPost httpPost;List<NameValuePair> nvps;
            httpPost = new HttpPost("http://localhost:5000/overview");
            nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("action", "children"));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps);
            httpPost.setEntity(entity);
            return httpPost;
        }

        private HttpPost pollRequest() throws UnsupportedEncodingException {
            HttpPost httpPost;
            List<NameValuePair> nvps;
            httpPost = new HttpPost("http://localhost:5000/overview");
            nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("action", "poll"));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps);
            httpPost.setEntity(entity);
            return httpPost;
        }

        private HttpPost editChildRequest() throws UnsupportedEncodingException {
            List<NameValuePair> nvps = new ArrayList<>();
            HttpPost httpPost = new HttpPost("http://localhost:5000/child");

            Child updated = db.select(Child.class, new Where(DatabaseColumns.gc_grownup_id, " = ", grownupId)).get(0);

            nvps.add(new BasicNameValuePair("action", "update"));
            nvps.add(new BasicNameValuePair("child_id", String.valueOf(updated.getChildId())));
            nvps.add(new BasicNameValuePair("nickname", updated.getNickname()));
            nvps.add(new BasicNameValuePair("child_first_name", updated.getFirstName()));
            nvps.add(new BasicNameValuePair("child_middle_name", updated.getMiddleName()));
            nvps.add(new BasicNameValuePair("child_last_name", updated.getLastName()));
            nvps.add(new BasicNameValuePair("color", updated.getColor()));

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps);
            httpPost.setEntity(entity);
            return httpPost;
        }

    }
}


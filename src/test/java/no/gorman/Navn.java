package no.gorman;

import no.gorman.database.*;
import no.gorman.please.common.Child;
import no.gorman.please.common.DayCareCenter;
import no.gorman.please.common.GrownUp;
import no.gorman.please.overview.Schedule;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class Navn {
    private final static String[] jentenavn = new String[] {"ABIGAIL","ADA","ADELE","ADINA","ADINE","ADRIANA","AGATHE","AGNES","AGNETE","AGNETHE","AIDA",
            "AILIN","AIMEE","AINA","AISHA","AJLA","ALBA","ALBERTINE","ALEA","ALEKSANDRA","ALETTE","ALEXANDRA","ALICE","ALICIA","ALICJA","ALIDA","ALINA",
            "ALINE","ALISA","ALISE","ALISHA","ALMA","ALVA","ALVILDE","AMAL","AMALIA","AMALIE","AMANDA","AMELIA","AMELIE","AMINA","AMIRA","AMNA","AMY",
            "ANA","ANASTASIA","ANDREA","ANDRINE","ANE","ANEA","ANETTE","ANGELA","ANGELICA","ANGELIKA","ANGELINA","ANINE","ANISA","ANITA","ANJA","ANN",
            "ANNA","ANNABEL","ANNABELL","ANNABELLE","ANNE","ANNELI","ANNIE","ANNIKA","ANNIKEN","ARIA","ARIANA","ARIANE","ARIEL","ASTRI","ASTRID","AURORA",
            "AVA","AYA","AYLA","AYLIN","BEATRICE","BELLA","BENEDICTE","BENEDIKTE","BERIT","BERTINE","BETINA","BETTINA","BIANCA","BIRGITTE","CAMILLA","CARINA",
            "CARMEN","CAROLINE","CASSANDRA","CATHRINE","CECILIA","CECILIE","CELIA","CELIN","CELINA","CELINE","CHARLOTTE","CHLOE","CHRISTIANE","CHRISTINA",
            "CHRISTINE","CINDY","CLARA","CORNELIA","DANIA","DANIELA","DANIELLA","DELINA","DIANA","DINA","DORTHE","DORTHEA","EA","EBBA","EDEL","EDITH","EILEEN",
            "EIR","EIRA","EIRIL","EIRILL","EIRIN","EIVOR","ELA","ELEA","ELENA","ELI","ELIANA","ELIDA","ELIN","ELINA","ELINE","ELISA","ELISABETH","ELISE","ELIZA",
            "ELIZABETH","ELLA","ELLE","ELLEN","ELLIE","ELLINOR","ELSA","ELSE","ELVIRA","EMA","EMBLA","EMELIE","EMELINE","EMELY","EMILIA","EMILIE","EMILIJA","EMILY",
            "EMINA","EMINE","EMMA","EMMELI","EMMELIN","EMMELINE","EMMY","ENYA","ERICA","ERIKA","ERLE","ESTER","ESTHER","EVA","EVELINA","EVELYN","FANNY","FATIMA",
            "FELICIA","FILIPPA","FIONA","FREDRIKKE","FREJA","FREYA","FRIDA","FRIDE","FRØYA","FRØYDIS","GABIJA","GABRIELA","GABRIELLA","GINA","GUNHILD","GURO",
            "HAFSA","HANA","HANNA","HANNAH","HANNE","HEDDA","HEDVIG","HEGE","HEIDI","HELEN","HELENA","HELENE","HELIN","HELLE","HENNIE","HENNY","HENRIETTE","HENRIKKE",
            "HERMINE","HILDE","IBEN","IDA","IDUN","IDUNN","IMAN","INA","INE","INES","INGA","INGEBJØRG","INGEBORG","INGELIN","INGER","INGRI","INGRID","INGUNN","INGVILD",
            "IQRA","IRENE","IRIS","IRMELIN","ISA","ISABEL","ISABELL","ISABELLA","ISABELLE","ISELIN","ISRA","JANNE","JASMIN","JASMINE","JEANETTE","JENNIE","JENNIFER","JENNY",
            "JESSICA","JOHANNA","JOHANNE","JOSEFINE","JOSEPHINE","JULIA","JULIANE","JULIANNE","JULIE","JUNE","JUNI","KAIA","KAISA","KAJA","KAJSA","KAMILE","KAMILLA","KAREN",
            "KARI","KARIANNE","KARIN","KARINA","KARINE","KAROLINA","KAROLINE","KATARINA","KATHARINA","KATHRINE","KATINKA","KATJA","KATRINE","KAYA","KAYLA","KHADIJA","KINE",
            "KINGA","KIRA","KJERSTI","KLARA","KLAUDIA","KORNELIA","KRISTIANE","KRISTIN","KRISTINA","KRISTINE","LAIBA","LAILA","LANA","LARA","LAURA","LEA","LEAH","LEANA",
            "LEILA","LENA","LENE","LEONA","LEONORA","LERKE","LIANA","LILIANA","LILJA","LILJE","LILLI","LILLIAN","LILLY","LILY","LINA","LINDA","LINDE","LINE","LINEA","LINN",
            "LINNEA","LISA","LISE","LIV","LIVA","LIVE","LIVIA","LONE","LOTTA","LOTTE","LOUISE","LOVISE","LUCIA","LUCY","LUNA","LYCKE","LYDIA","LYKKE","LÆRKE","MADELEINE",
            "MADELEN","MADELENE","MAGDALENA","MAI","MAIA","MAIDA","MAIKEN","MAJA","MALAIKA","MALAK","MALENA","MALENE","MALI","MALIN","MAREN","MARGIT","MARGRETE","MARGRETHE",
            "MARI","MARIA","MARIAM","MARIANNE","MARIE","MARIEL","MARIELL","MARIELLE","MARINA","MARION","MARIT","MARITA","MARLENE","MARTA","MARTE","MARTHA","MARTHE","MARTHINE",
            "MARTINA","MARTINE","MARTYNA","MARWA","MARY","MARYAM","MATHEA","MATHILDA","MATHILDE","MATILDA","MATILDE","MAUD","MAY","MAYA","MEDINA","MELINA","MELINDA","MELISA",
            "MELISSA","MIA","MICHELLE","MIE","MILA","MILENA","MILJA","MILLA","MILLE","MILLIE","MINA","MIRA","MIRIAM","MOA","MOLLY","MONA","MONICA","MUNA","NADIA","NAJMA","NANNA",
            "NAOMI","NATALIA","NATALIE","NATASHA","NATHALIE","NELLIE","NELLY","NICOLE","NICOLINE","NIKOLA","NIKOLINE","NILA","NINA","NOOR","NORA","NORAH","NOVA","ODA","OLAVA","OLEA",
            "OLINE","OLIVIA","OLIWIA","OTHELIE","OTHILIE","OTILIE","PATRYCJA","PAULA","PAULINA","PAULINE","PERNILLE","PETRA","PIA","RACHEL","RAGNA","RAGNHILD","RAKEL","RAMONA","RANDI",
            "RANIA","REBECCA","REBECKA","REBEKKA","REGINE","RENATE","RIKKE","RONJA","ROSE","RUNA","RUTH","SABRINA","SAFA","SAGA","SAHRA","SALMA","SAMANTHA","SAMIRA","SANDRA","SANNA",
            "SANNE","SARA","SARAH","SAVANNAH","SELENA","SELIN","SELINA","SELINE","SELMA","SERINA","SERINE","SIENNA","SIGNE","SIGRID","SIGRUN","SILJA","SILJE","SIMONE","SINA","SINE",
            "SIREN","SIRI","SIRIL","SIV","SOFIA","SOFIE","SOL","SOLVEIG","SONIA","SONJA","SOPHIA","SOPHIE","STELLA","STINA","STINE","SUMAYA","SUNNIVA","SUSANN","SUSANNA","SUSANNE",
            "SYNNE","SYNNØVE","TALE","TARA","TEA","TERESE","THALE","THEA","THELMA","THERESE","THILDE","THORA","TIA","TILDA","TILDE","TILJE","TILLA","TINA","TINDRA","TINE","TIRIL",
            "TIRILL","TOMINE","TONE","TONJE","TORA","TOVE","TRINE","TRUDE","TUVA","TYRA","UGNE","ULLA","ULRIKKE","UNA","VALENTINA","VANESSA","VANJA","VERA","VERONICA","VERONIKA",
            "VESLEMØY","VICTORIA","VIDA","VIKTORIA","VILDE","VILJA","VILJE","VILMA","VIOLA","VIVIAN","VÅR","VÅRIN","WERONIKA","WIKTORIA","WILMA","YASMIN","YLVA","YUSRA","ÅSE",
            "ÅSHILD","ÅSNE","ZAHRA","ZAINAB","ZARA","ZOE","ZOFIA","ZUZANNA"};

private static final String[] guttenavn = new String[]{
"AARON","ABDIRAHMAN","ABDUL","ABDULLAH","ABDULLAHI","ABEL","ADAM","ADNAN","ADRIAN","AHMAD","AHMED","AIDEN","AILO","AKSEL","ALAN","ALBERT","ALBIN","ALEKSANDER","ALEX","ALEXANDER",
        "ALF","ALFRED","ALI","ALLAN","ALVIN","AMADEUS","AMANDUS","AMAR","AMIN","AMIR","AMUND","ANAS","ANDERS","ANDRE","ANDREAS","ANTHONY","ANTON","ANTONI","ANTONIO","ARE","ARIAN",
        "ARILD","ARIN","ARMAN","ARN","ARNE","ARON","ARTHUR","ARTUR","ARVID","ARVIN","ARYAN","ASBJØRN","ASGEIR","ASK","ASKIL","ASLAK","ATLE","AUDUN","AUGUST","AUNE","AXEL","AYMAN",
        "AYUB","BALDER","BARTOSZ","BASTIAN","BENDIK","BENJAMIN","BERNHARD","BILAL","BIRK","BJARNE","BJØRN","BJØRNAR","BO","BRAGE","BREDE","BRIAN","BROR","BRYNJAR","BØRGE","BÅRD",
        "CARL","CARLOS","CASPER","CASPIAN","CHARLIE","CHRIS","CHRISANDER","CHRISTER","CHRISTIAN","CHRISTOFFER","CHRISTOPHER","COLIN","CONRAD","CORNELIUS","DAG","DAMIAN","DAN","DANI",
        "DANIEL","DARIN","DAVID","DAWID","DENIS","DENNIS","DIDRIK","DOMINIC","DOMINIK","EDGAR","EDIN","EDVALD","EDVARD","EDVIN","EDWARD","EDWIN","EGIL","EILIF","EINAR","EIRIK",
        "EIVIND","ELDAR","ELIAH","ELIAS","ELLIOT","ELLIOTT","EMANUEL","EMIL","EMILIAN","EMILIO","EMIR","EMRE","EMRIK","ENDRE","EREN","ERIC","ERIK","ERLEND","ERLING","ESKIL","ESKILD",
        "ESPEN","EVAN","EVEN","FABIAN","FALK","FELIX","FERDINAND","FILIP","FILLIP","FINN","FRANCISZEK","FRANK","FREDERIK","FREDRICK","FREDRIK","GABRIEL","GARD","GAUTE","GEIR","GEORG",
        "GJERMUND","GLENN","GUNNAR","GUSTAV","GØRAN","HAAKON","HALLVARD","HALVARD","HALVOR","HAMZA","HANS","HARALD","HASAN","HASSAN","HAUK","HEINE","HELGE","HELMER","HENNING","HENRIK",
        "HENRY","HERMAN","HERMANN","HUGO","HUSSEIN","HÅKON","HÅVAR","HÅVARD","IAN","IBEN","IBRAHIM","ILYAS","IMRAN","IMRE","INGMAR","ISA","ISAAC","ISAC","ISAK","ISMAIL","IVAN","IVAR",
        "IVER","IVO","JACK","JACOB","JAKOB","JAKUB","JAMES","JAMIE","JAN","JARAN","JARAND","JARLE","JASPER","JAYDEN","JENS","JEPPE","JESPER","JIM","JIMMY","JO","JOACHIM","JOAKIM","JOAR",
        "JOEL","JOHAN","JOHANN","JOHANNES","JOHN","JOHNNY","JON","JONAH","JONAS","JONATAN","JONATHAN","JONE","JOSEF","JOSEPH","JOSHUA","JOSTEIN","JULIAN","JULIUS","JUSTIN","JØRAN","JØRGEN",
        "KACPER","KAI","KAJUS","KAMIL","KARL","KARSTEN","KASPAR","KASPER","KASPIAN","KEN","KENAN","KENNETH","KENT","KEVIN","KHALID","KIAN","KIM","KJELL","KJETIL","KNUT","KONRAD","KORNELIUS",
        "KRISTER","KRISTIAN","KRISTOFFER","KRYSTIAN","KYRRE","KÅRE","LARS","LASSE","LAURITS","LAURITZ","LAVRANS","LEANDER","LEIF","LEO","LEON","LEONARD","LEONARDO","LEVI","LIAM","LINUS","LIONEL",
        "LOKE","LOUIS","LUCA","LUCAS","LUDVIG","LUDVIK","LUIS","LUKA","LUKAS","MACIEJ","MADS","MAGNE","MAGNUS","MAHAD","MAHAMED","MAHDI","MAKSYMILIAN","MARCEL","MARCO","MARCUS","MARIO","MARIUS",
        "MARKUS","MARTIN","MARTINIUS","MARTINUS","MATAS","MATEO","MATEUSZ","MATHEO","MATHEUS","MATHIAS","MATIAS","MATS","MATTEO","MATTEUS","MATTHEW","MATTIAS","MATTIS","MAX","MAXIM","MAXIMILIAN",
        "MAXIMILLIAN","MELVIN","MICHAEL","MICHAL","MIKA","MIKAEL","MIKAIL","MIKAL","MIKKEL","MIKOLAJ","MILAN","MILIAN","MILO","MIO","MOHAMED","MOHAMMAD","MOHAMMED","MONS","MORGAN","MORTEN",
        "MUHAMMAD","MUHAMMED","MUSTAFA","NATAN","NATANIEL","NATHAN","NATHANIEL","NEO","NICHLAS","NICHOLAS","NICKLAS","NICKOLAI","NICLAS","NICO","NICOLAI","NICOLAS","NICOLAY","NIKLAS","NIKOLAI",
        "NIKOLAS","NIKOLAY","NILS","NJÅL","NOA","NOAH","NOEL","NOJUS","ODD","ODIN","OLA","OLAF","OLAI","OLANDER","OLAV","OLE","OLIVER","OLIVIER","OLIWIER","OLVE","OMAR","OSCAR","OSKAR","OTTO",
        "PATRICK","PATRIK","PATRYK","PAUL","PEDER","PELLE","PER","PETER","PETTER","PHILIP","PHILLIP","PIOTR","PREBEN","PÅL","RAFAEL","RAGNAR","RASMUS","RAVN","RAYAN","REMI","RICHARD","ROBERT",
        "ROBIN","ROLF","RUBEN","RUNAR","RUNE","RYAN","SAID","SAKARIAS","SAM","SAMSON","SAMUEL","SANDER","SCOTT","SEAN","SEBASTIAN","SELMER","SEVERIN","SHAWN","SIGMUND","SIGURD","SIGVE","SILAS",
        "SIMEN","SIMON","SINDRE","SIVERT","SJUR","SNORRE","SOFUS","SOLAN","SONDRE","STEFAN","STEFFEN","STEIN","STEINAR","STIAN","STIG","STORM","STURLA","SUNE","SVEIN","SVEINUNG","SVEN","SVERRE",
        "SYED","SYVER","SØLVE","SZYMON","TAGE","TAHA","TALLAK","TARALD","TARIK","TARJEI","TEO","TEODOR","TERJE","THEO","THEODOR","THOMAS","THOR","THORBJØRN","THORVALD","TIAN","TIM","TIMIAN",
        "TINIUS","TOBIAS","TOM","TOMAS","TOMMY","TONY","TOR","TORBEN","TORBJØRN","TORD","TORE","TORGEIR","TORJE","TORJUS","TORMOD","TORSTEIN","TRISTAN","TROND","TROY","TRULS","TRYGVE","TRYM",
        "ULRIK","VARG","VEBJØRN","VEGAR","VEGARD","VEMUND","VETLE","VICTOR","VIDAR","VIKTOR","VILHELM","VILJAR","VILMER","VINCENT","VINJAR","WIKTOR","WILHELM","WILLIAM","WILMER","YAHYA","YASIN",
        "YASSIN","YNGVE","YOSEF","YOUNES","YOUSEF","YUSUF","ØRJAN","ØYSTEIN","ØYVIND","ÅDNE","ÅSMUND","ZAKARIA","ZANDER"};


    public static final String[] lastnames = new String[] {
"Hansen", "Johansen", "Olsen", "Larsen", "Andersen", "Pedersen", "Nilsen", "Kristiansen", "Jensen", "Karlsen", "Johnsen", "Pettersen", "Eriksen", "Berg", "Haugen",
            "Hagen", "Johannessen", "Andreassen", "Jacobsen", "Halvorsen", "Jørgensen", "Dahl", "Henriksen", "Lund", "Sørensen", "Jakobsen", "Gundersen", "Moen", "Iversen",
            "Svendsen", "Strand", "Solberg", "Martinsen", "Knutsen", "Paulsen", "Eide", "Bakken", "Kristoffersen", "Mathisen", "Lie", "Rasmussen", "Amundsen", "Lunde",
            "Kristensen", "Bakke", "Berge", "Moe", "Nygård", "Fredriksen", "Solheim", "Lien", "Holm", "Nguyen", "Andresen", "Christensen", "Knudsen", "Hauge", "Nielsen",
            "Evensen", "Sæther", "Aas", "Hanssen", "Myhre", "Thomassen", "Haugland", "Simonsen", "Sivertsen", "Berntsen", "Danielsen", "Arnesen", "Rønning", "Næss", "Sandvik",
            "Antonsen", "Haug", "Ellingsen", "Edvardsen", "Thorsen", "Vik", "Ali", "Gulbrandsen", "Ruud", "Isaksen", "Birkeland", "Strøm", "Aasen", "Ødegård", "Jenssen", "Tangen",
            "Eliassen", "Myklebust", "Bøe", "Mikkelsen", "Aune", "Ahmed", "Helland", "Tveit", "Abrahamsen", "Brekke", "Engen", "Madsen", "Christiansen", "Sunde", "Mortensen",
            "Thoresen", "Bjerke", "Torgersen", "Hermansen", "Mikalsen", "Magnussen", "Nilssen", "Helgesen", "Bråthen", "Gjerde", "Hovland", "Wold", "Eilertsen", "Dahle", "Nygaard",
            "Dale", "Steen", "Wilhelmsen", "Jansen", "Foss", "Bjørnstad", "Gabrielsen", "Sætre", "Gustavsen", "Håland", "Hammer", "Ingebrigtsen", "Bråten", "Hoel", "Engebretsen",
            "Solli", "Carlsen", "Holmen", "Samuelsen", "Hoff", "Lorentzen", "Tran", "Ludvigsen", "Sveen", "Sandnes", "Rønningen", "Monsen", "Fossum", "Breivik", "Fjeld", "Johannesen",
            "Solvang", "Solbakken", "Nordby", "Jonassen", "Syversen", "Stokke", "Bye", "Sand", "Egeland", "Aase", "Dalen", "Andersson", "Løken", "Bø", "Ødegaard", "Sørlie", "Wiik",
            "Johansson", "Haga", "Sandberg", "Tollefsen", "Møller", "Viken", "Hassan", "Haaland", "Teigen", "Wang", "Kvam", "Hamre", "Torp", "Enger", "Berger", "Kolstad", "Borge", "Sande",
            "Nikolaisen", "Ottesen", "Stene", "Øien", "Eikeland", "Holen", "Kvamme", "Helle", "Fosse", "Borgen", "Khan", "Sletten", "Langeland", "Mohamed", "Skoglund", "Haugan", "Kleven",
            "Petersen", "Løkken", "Arntsen", "Markussen", "Tønnessen", "Lind", "Mæland", "Hole", "Stensrud", "Arntzen", "Østby", "Aamodt", "Nordli", "Lian", "Tvedt", "Røed", "Solem",
            "Ellefsen", "Myrvang", "Husby", "Berget", "Syvertsen", "Eggen", "Davidsen", "Vold", "Simensen", "Børresen", "Nesse", "Hetland", "Ness", "Skaar", "Kleppe", "Marthinsen", "Gran",
            "Stenberg", "Finstad", "Fjeldstad", "Sund", "Reitan", "Olaussen", "Meyer", "Smith", "Brevik", "Norheim", "Larssen", "Holte", "Skaug", "Bjelland", "Gjertsen", "Le", "Meland",
            "Krogstad", "Klausen", "Espeland", "Bolstad", "Riise", "Nilsson", "Hussain", "Solhaug", "Lauritzen", "Nordbø", "Mo", "Lindberg", "Rustad", "Olafsen", "Endresen", "Bendiksen",
            "Grimstad", "Farstad", "Waage", "Krogh", "Holt", "Thorvaldsen", "Bergersen", "Aasheim", "Opsahl", "Vatne", "Myrvold", "Larsson", "Gulliksen", "Grande", "Øverland", "Mohammed",
            "Nyborg", "Thorstensen", "Bredesen", "Salvesen", "Stangeland", "Sørli", "Rød", "Thorbjørnsen", "Normann", "Nordvik", "Nyhus", "Ahmad", "Ringstad", "Steffensen", "Frantzen",
            "Bjørnsen", "Hjelle", "Nordahl", "Holter", "Melby", "Haraldsen", "Sørum", "Abdi", "Mathiesen", "Mork", "Lervik", "Skogen", "Christoffersen", "Nes", "Hovde", "Håkonsen",
            "Opheim", "Mørk", "Ramstad", "Eriksson", "Skår", "Ibrahim", "Karlsson", "Økland", "Øyen", "Stenersen", "Lande", "Guttormsen", "Volden", "Fredheim", "Sundby", "Skeie", "Erlandsen",
            "Holst", "Bugge", "Vangen", "Braathen", "Bergan", "Robertsen", "Bang", "Singh", "Nymoen", "Kaspersen", "Ask", "Martinussen", "Johnsrud", "Olsson", "Høyland", "Rørvik",
            "Hussein", "Bjørklund", "Aronsen", "Lundberg", "Bjørge", "Våge", "Hope", "Michelsen", "Berglund", "Wiig", "Grønvold", "Hovden", "Dahlen", "Lid", "Strømme", "Strømmen",
            "Sæbø", "Holthe", "Skjæveland", "Kleiven", "Tobiassen", "Seim", "Tveiten", "Pham", "Engh", "Clausen", "Nicolaisen", "Søvik", "Skogstad", "Ervik", "Sagen", "Askeland",
            "Solstad", "Skar", "Rogne", "Bekkevold", "Benjaminsen", "Austad", "Nyheim", "Meling", "Løland", "Gravdal", "Øvrebø", "Nordal", "Nyland", "Lauritsen", "Bertelsen", "Stensland",
            "Olstad", "Steinsland", "Didriksen", "Schei", "Borgersen", "Lyngstad", "Brenden", "Kaur", "Thorkildsen", "Barstad", "Bentsen", "Nordgård", "Ramberg", "Vinje", "Svensson",
            "Odden", "Nesheim", "Ingvaldsen", "Norum", "Bekken", "Langseth", "Persson", "Enoksen", "Lia", "Frydenlund", "Iqbal", "Jørstad", "Huse", "Sæter", "Midtbø", "Thomsen", "Nordheim",
            "Evjen", "Bergh", "Tandberg", "Eidem", "Heimdal", "Omar", "Stenseth", "Hoem", "Horn", "Haugerud", "Winther", "Alstad", "Gjerstad", "Førde", "Fossen", "Aasland", "Aslaksen",
            "Westby", "Huseby", "Fagerli", "Ueland", "Korneliussen", "Svensen", "Bergli", "Berntzen", "Lange", "Hopland", "Øye", "Heggelund", "Handeland", "Nicolaysen", "Bruun", "Blindheim",
            "Brun", "Sollie", "Almås", "Gilje", "Myren", "Kvalheim", "Øverby", "Hågensen", "Lystad", "Holten", "Furnes", "Lorentsen", "Brenna", "Reiersen", "Hustad", "Gregersen", "Skog",
            "Selnes", "Granli", "Hove", "Tønnesen", "Nergård", "Nerland", "Bjørndal", "Åsheim", "Lohne", "Trondsen", "Bergum", "Sjursen", "Grindheim", "Flaten", "Torsvik", "Liland", "Blom",
            "Bergheim", "Sundal", "Løvås", "Vågen"};


    private final static List<String> clubNames = Arrays.asList("furua", "rogna", "grana", "bjørka");
    private final static List<String> aldersgrupper = Arrays.asList("spurv", "meis", "hakkespett", "kråke", "skolespire");

    private final static String[] colors = new String[]{
            String.format("#%06X", (0xFFFFFF & new Color(192, 192, 192).getRGB())),
            String.format("#%06X", (0xFFFFFF & new Color(128, 128, 128).getRGB())),
            String.format("#%06X", (0xFFFFFF & new Color(64, 64, 64).getRGB())),
            String.format("#%06X", (0xFFFFFF & new Color(255, 0, 0).getRGB())),
            String.format("#%06X", (0xFFFFFF & new Color(255, 175, 175).getRGB())),
            String.format("#%06X", (0xFFFFFF & new Color(255, 200, 0).getRGB())),
            String.format("#%06X", (0xFFFFFF & new Color(255, 255, 0).getRGB())),
            String.format("#%06X", (0xFFFFFF & new Color(0, 255, 0).getRGB())),
            String.format("#%06X", (0xFFFFFF & new Color(255, 0, 255).getRGB())),
            String.format("#%06X", (0xFFFFFF & new Color(0, 255, 255).getRGB())),
            String.format("#%06X", (0xFFFFFF & new Color(0, 0, 255).getRGB()))};

    public static void main(String[] args) {
        DBFunctions.setupConnectionPool("jdbc:postgresql://localhost/bax?useUnicode=true&characterEncoding=utf8", "postgres", "baxter", 3);
        insertEmployees();
    }


    private static void insertEmployees() {
        DB db = new DB(DBFunctions.getConnection());
        try {
            Random rnd = new Random();
            for (DayCareCenter daycare : db.select(DayCareCenter.class)){
                Map<String, Club> aldersklubber = new HashMap<>();
                aldersgrupper.forEach(gruppe -> {
                    Club aldersgruppe = new Club();
                    aldersgruppe.club_color = colors[rnd.nextInt(colors.length)];
                    aldersgruppe.club_name = gruppe;
                    aldersgruppe.club_daycare_id = daycare.getDayCareCenterId();
                    db.insert(aldersgruppe);
                    aldersklubber.put(gruppe, aldersgruppe);
                });
                clubNames.forEach(avdeling -> {
                    Club club = new Club();
                    club.club_name = avdeling;
                    club.club_daycare_id = daycare.getDayCareCenterId();
                    club.club_color = colors[rnd.nextInt(colors.length)];
                    db.insert(club);

                    List<Child> children = new ArrayList<>();
                    for (int i = 0; i < 10; i++) {
                        boolean siblings = rnd.nextBoolean();
                        Child gutt = insertChild(db, rnd, daycare, guttenavn, Optional.empty());
                        children.add(gutt);
                        db.link(gutt, club);
                        Optional<Child> sibling = siblings ? Optional.of(gutt) : Optional.empty();
                        Child jente = insertChild(db, rnd, daycare, jentenavn, sibling);
                        children.add(jente);
                        db.link(jente, club);
                    }

                    for (Child c : children) {
                        int idx = LocalDate.now().getYear() - c.getDOB().getYear() - 1;
                        idx = Math.min(4, Math.max(idx,0));
                        db.link(c, aldersklubber.get(aldersgrupper.get(idx)));
                    }

                    for (int i = 0; i < 5; i++) {
                        int lastnameIdx = rnd.nextInt(lastnames.length);
                        String[] nameArray = (rnd.nextBoolean()) ? jentenavn : guttenavn;
                        GrownUp employee = new GrownUp();
                        employee.setFirstName(StringUtils.capitalize(nameArray[rnd.nextInt(nameArray.length)].toLowerCase()));
                        employee.setLastName(lastnames[lastnameIdx]);
                        employee.setEmail(employee.getFirstName().toLowerCase() + "." + employee.getLastName().toLowerCase() + "@" + StringUtils.deleteWhitespace(daycare.getDayCareName().toLowerCase()) + ".no");
                        employee.setPassword(employee.getFirstName());
                        employee.setDayCareId(daycare.getDayCareCenterId());
                        db.insert(employee);
                        db.link(employee, club);
                        children.forEach(child -> db.link(employee, child));
                    }
                });
            }
            db.commitAndReleaseConnection();
        }catch(Exception e) {
            e.printStackTrace();
            db.commitAndReleaseConnection();
        }
        System.exit(0);
    }



    private static Child insertChild(DB db, Random rnd, DayCareCenter daycare, String[] firstnames, Optional<Child> sibling) {
        Child child = new Child();
        int idx = rnd.nextInt(firstnames.length);
        int lastnameIdx = rnd.nextInt(lastnames.length);
        String lastName = sibling.isPresent()?sibling.get().getLastName():StringUtils.capitalize(lastnames[lastnameIdx].toLowerCase());
        child.setFirstName(StringUtils.capitalize(firstnames[idx].toLowerCase()));
        child.setLastName(lastName);
        child.setNickname(child.getFirstName());
        child.setDaycareId(daycare.getDayCareCenterId());
        child.setColor(colors[rnd.nextInt(colors.length)]);
        child.setDOB(LocalDate.now().withDayOfYear(1).minusYears(rnd.nextInt(6)).plusMonths(rnd.nextInt(12)).plusDays(rnd.nextInt(28)));
        db.insert(child);

        if (!sibling.isPresent()) {
            GrownUp mamma = new GrownUp();
            mamma.setFirstName(StringUtils.capitalize(jentenavn[rnd.nextInt(jentenavn.length)].toLowerCase()));
            mamma.setLastName(child.getLastName());
            mamma.setEmail(mamma.getFirstName().toLowerCase() + "." + mamma.getLastName().toLowerCase() + "@email.com");
            mamma.setPassword(mamma.getFirstName());
            mamma.setDayCareId(daycare.getDayCareCenterId());
            db.insert(mamma);
            db.link(child, mamma);

            GrownUp pappa = new GrownUp();
            pappa.setFirstName(StringUtils.capitalize(guttenavn[rnd.nextInt(guttenavn.length)].toLowerCase()));
            pappa.setLastName(child.getLastName());
            pappa.setEmail(pappa.getFirstName().toLowerCase() + "." + pappa.getLastName().toLowerCase() + "@email.com");
            pappa.setPassword(pappa.getFirstName());
            pappa.setDayCareId(daycare.getDayCareCenterId());
            db.insert(pappa);
            db.link(child, pappa);

        }else {
            db.select(GrownUp.class, new Where(DatabaseColumns.gc_child_id, " = ", sibling.get().getChildId())).forEach(grownup -> db.link( child, grownup));
        }

        Schedule schedule = new Schedule();
        schedule.setName("Child spotting");
        schedule.setInterval(10);
        schedule.setChildId(child.getChildId());
        db.insert(schedule);

        if (LocalDate.now().getYear() - child.getDOB().getYear() <= 3) {
            Schedule diaperChange = new Schedule();
            diaperChange.setName("Diaper change");
            diaperChange.setInterval(180);
            diaperChange.setChildId(child.getChildId());
            db.insert(diaperChange);
        }
        return child;
    }
    public static class Club {
    @Column(column=DatabaseColumns.club_id)
    private Long club_id;

    @Column(column=DatabaseColumns.club_name)
    private String club_name;

    @Column(column=DatabaseColumns.club_color)
    private String club_color;

    @Column(column=DatabaseColumns.club_daycare_id)
    private Long club_daycare_id;
    }
}


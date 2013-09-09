package no.gorman.database;

import java.lang.ref.WeakReference;

import no.gorman.database.BigBrother.Spy;
import no.gorman.please.common.Child;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BigBrotherTest {

    Child you = new Child();
    
    @Before
    public void setUp() {
        you.setChildId(1l);
    }
    
    @Test
    public void removes_entry_when_all_spies_are_dead() throws InterruptedException {
        WeakReference<Spy> spyRef = addSpy();
        Assert.assertEquals(1, BigBrother.getSpies(new BigBrother.RowIdentifier(DatabaseColumns.child_id, 1l)).get().size());
        while (spyRef.get() != null) {
            System.gc();
        }
        Thread.sleep(50);
        Assert.assertFalse(BigBrother.getSpies(new BigBrother.RowIdentifier(DatabaseColumns.child_id, 1l)).isPresent());
    }

    private WeakReference<Spy> addSpy() {
        Spy spy = new Spy() {
            @Override
            public void suspectAltered(Object suspect) {
                
            }};
        BigBrother.spyOn(you, spy);
        return new WeakReference<Spy>(spy);
    }

}

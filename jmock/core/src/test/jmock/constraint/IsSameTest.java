package test.jmock.constraint;

import org.jmock.constraint.IsSame;


public class IsSameTest extends ConstraintsTest {
    public void testEvaluatesToTrueIfArgumentIsReferenceToASpecifiedObject() {
        Object o1 = new Object();
        Object o2 = new Object();
        
        IsSame isSame = new IsSame(o1);
        
        assertTrue(isSame.eval(o1));
        assertFalse(isSame.eval(o2));
    }
    
    public void testReturnsReadableDescriptionFromToString() {
        IsSame isSame = new IsSame("ARG");
        assertEquals( "description", "same(<ARG>)", isSame.toString() );
    }
    
    public void testReturnsReadableDescriptionFromToStringWhenInitialisedWithNull() {
        IsSame isSame = new IsSame(null);
        assertEquals( "description", "same(null)", isSame.toString() );
    }
}

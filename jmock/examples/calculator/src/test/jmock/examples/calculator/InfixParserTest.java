/* Copyright Dec 7, 2003 Nat Pryce, all rights reserved.
 */
package test.jmock.examples.calculator;


import org.jmock.C;
import org.jmock.dynamock.Mock;

import junit.framework.TestCase;

import org.jmock.examples.calculator.Expression;
import org.jmock.examples.calculator.ExpressionFactory;
import org.jmock.examples.calculator.InfixParser;
import org.jmock.examples.calculator.ParseException;
import org.jmock.examples.calculator.SimpleEnvironment;
import org.jmock.util.Verifier;


public class InfixParserTest 
    extends TestCase 
{
    private Mock mockExpressionFactory;
    private InfixParser parser;
    private SimpleEnvironment environment;
    private Expression mockLiteral1 = dummyExpression("mockLiteral1");
    private Expression mockLiteral2 = dummyExpression("mockLiteral2");
    private Expression mockAddition = dummyExpression("mockAddition");
    private Expression mockSubtraction = dummyExpression("mockSubtraction");
    private Expression mockMultiplication = dummyExpression("mockMultiplication");
    private Expression mockDivision = dummyExpression("mockDivision");
    private Expression mockPower = dummyExpression("mockPower");
    private Expression mockVariableReference = dummyExpression("mockVariableReference");
    
    
    public void setUp() {
        mockExpressionFactory = new Mock(ExpressionFactory.class);
        parser = new InfixParser( (ExpressionFactory)mockExpressionFactory.proxy() );
        environment = new SimpleEnvironment();
    }
    
    public void verifyAll() {
        Verifier.verifyObject(this);
    }
    
    public void testParsesLiteral() throws Exception {
        mockExpressionFactory.expectAndReturn("newLiteral", C.args(C.eq(4.0)), mockLiteral1 );
        
        assertSame( "should be literal", mockLiteral1, parser.parse("4.0") );
        
        verifyAll();
    }
    
    public void testParsesVariableReference() throws Exception {
        mockExpressionFactory.expectAndReturn( "newVariableReference", "varName", 
                                               mockVariableReference );
        
        assertSame( "should be variable reference", 
                    mockVariableReference, parser.parse("varName") );
                    
        verifyAll();
    }
    
    public void testParsesAddition() throws Exception {
        mockExpressionFactory.expectAndReturn( "newLiteral", C.args(C.eq(1.0)), mockLiteral1 );
        mockExpressionFactory.expectAndReturn( "newLiteral", C.args(C.eq(2.0)), mockLiteral2 );
        mockExpressionFactory.expectAndReturn( "newAddition", 
            C.eq(mockLiteral1,mockLiteral2), mockAddition );
        
        assertSame( "should be addition", mockAddition, parser.parse("1+2") );
        
        verifyAll();
    }

    public void testThrowsExceptionForInvalidAdditionSyntax() throws Exception {
        mockExpressionFactory.expectAndReturn( "newLiteral", C.args(C.eq(1.0)), mockLiteral1 );
        
        try {
            parser.parse("1+");
            fail("ParseException expected when missing rhs");
        }
        catch( ParseException expected ) {}
        
        try {
            parser.parse("+2");
            fail("ParseException expected when missing lhs");
        }
        catch( ParseException expected ) {}
        
        verifyAll();
    }

    public void testParsesSubtraction() throws Exception {
        mockExpressionFactory.expectAndReturn( "newLiteral", C.args(C.eq(1.0)), mockLiteral1 );
        mockExpressionFactory.expectAndReturn( "newLiteral", C.args(C.eq(2.0)), mockLiteral2 );
        mockExpressionFactory.expectAndReturn( "newSubtraction", 
            C.eq(mockLiteral1,mockLiteral2), mockSubtraction);
        
        assertSame( "should be addition", mockSubtraction, parser.parse("1-2") );
        
        verifyAll();
    }
    
    public void testThrowsExceptionForInvalidSubtractionSyntax() throws Exception {
        mockExpressionFactory.expectAndReturn( "newLiteral", C.args(C.eq(1.0)), mockLiteral1 );
        try {
            parser.parse("1-");
            fail("ParseException expected when missing rhs");
        }
        catch( ParseException expected ) {}

        try {
            parser.parse("-2");
            fail("ParseException expected when missing lhs");
        }
        catch( ParseException expected ) {}
        
        verifyAll();
    }
    
    public void testParsesMultiplication() throws Exception {
        mockExpressionFactory.expectAndReturn( "newLiteral", C.args(C.eq(1.0)), mockLiteral1 );
        mockExpressionFactory.expectAndReturn( "newLiteral", C.args(C.eq(2.0)), mockLiteral2 );
        mockExpressionFactory.expectAndReturn( "newMultiplication", 
            C.eq(mockLiteral1,mockLiteral2), mockMultiplication);
        
        assertSame( "should be multiplication", mockMultiplication, parser.parse("1*2") );
        
        verifyAll();
    }
    
    public void testParsesDivision() throws Exception {
        mockExpressionFactory.expectAndReturn( "newLiteral", C.args(C.eq(1.0)), mockLiteral1 );
        mockExpressionFactory.expectAndReturn( "newLiteral", C.args(C.eq(2.0)), mockLiteral2 );
        mockExpressionFactory.expectAndReturn( "newDivision", 
            C.eq(mockLiteral1,mockLiteral2), mockDivision);
        
        assertSame( "should be division", mockDivision, parser.parse("1/2") );
        
        verifyAll();
    }
    
    public void testParsesPower() throws Exception {
        mockExpressionFactory.expectAndReturn( "newLiteral", C.args(C.eq(1.0)), mockLiteral1 );
        mockExpressionFactory.expectAndReturn( "newLiteral", C.args(C.eq(2.0)), mockLiteral2 );
        mockExpressionFactory.expectAndReturn( "newPower", 
            C.eq(mockLiteral1,mockLiteral2), mockPower);
        
        assertSame( "should be power", mockPower, parser.parse("1^2") );
        
        verifyAll();
    }
    
    public void testParseParenthesis() throws Exception {
        Expression xReference = dummyExpression("xReference");
        Expression yReference = dummyExpression("yReference");
        Expression zReference = dummyExpression("zReference");
        Expression addition = dummyExpression("addition");
        Expression multiplication = dummyExpression("multiplication");
        
        mockExpressionFactory.expectAndReturn("newVariableReference","x",xReference);
        mockExpressionFactory.expectAndReturn("newVariableReference","y",yReference);
        mockExpressionFactory.expectAndReturn("newVariableReference","z",zReference);
        mockExpressionFactory.expectAndReturn(
            "newAddition",C.eq(xReference,yReference), addition );
        mockExpressionFactory.expectAndReturn(
            "newMultiplication",C.eq(addition,zReference), multiplication );
        
        assertSame( "should be multiplication", 
                    multiplication, parser.parse("(x+y)*z") );
    }
    
    public void testThrowsExceptionIfNoClosingParenthesis() throws Exception {
        Expression xReference = dummyExpression("xReference");
        
        mockExpressionFactory.expectAndReturn("newVariableReference","x",xReference);
        
        try {
            parser.parse("(x");
            fail("ParseException expected");
        }
        catch( ParseException expected ) {}
    }
    
    private Expression dummyExpression( String name ) {
        return (Expression)new Mock( Expression.class, name ).proxy();
    }
}
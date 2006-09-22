package org.jmock;

import org.jmock.core.Action;
import org.jmock.core.Expectation;
import org.jmock.core.ExpectationError;
import org.jmock.core.ExpectationErrorTranslator;
import org.jmock.core.Imposteriser;
import org.jmock.core.Invocation;
import org.jmock.core.Invokable;
import org.jmock.core.MockObjectNamingScheme;
import org.jmock.internal.DispatcherControl;
import org.jmock.internal.ExpectationBuilder;
import org.jmock.internal.ExpectationCapture;
import org.jmock.internal.IdentityExpectationErrorTranslator;
import org.jmock.internal.InvocationDiverter;
import org.jmock.internal.UnspecifiedExpectation;
import org.jmock.lib.JavaReflectionImposteriser;
import org.jmock.lib.DefaultNamingScheme;
import org.jmock.lib.action.ReturnDefaultValueAction;


/**
 * A Mockery represents the context, or neighbourhood, of the object(s) under test.  
 * 
 * The neighbouring objects in that context are mocked out. The test specifies the 
 * expected interactions between the object(s) under test and its  neighbours and 
 * the Mockery checks those expectations while the test is running.
 * 
 * @author npryce
 * @author named by Ivan Moore.
 *
 */
public class Mockery {
    private Imposteriser imposteriser = new JavaReflectionImposteriser();
    private Action defaultAction = new ReturnDefaultValueAction(imposteriser);
    private ExpectationErrorTranslator expectationErrorTranslator = IdentityExpectationErrorTranslator.INSTANCE;
    private MockObjectNamingScheme namingScheme = DefaultNamingScheme.INSTANCE;
    
    private Expectation expectation = new UnspecifiedExpectation();
    private ExpectationCapture capture = null;
    private Throwable firstError = null;
    
    
    /* 
     * Policies
     */
    
    /**
     * Changes the action used be expectations for which no action has been explicity
     * defined.  
     * 
     * The default default action returns a value that is compatible with the method's
     * return type but may be null.
     */
    public void setDefaultAction(Action defaultAction) {
        this.defaultAction = defaultAction;
    }
    
    /**
     * Changes the imposteriser used to adapt mock objects to the mocked type.
     * 
     * The default imposteriser allows a test to mock interfaces but not
     * classes, so you'll have to plug a different imposteriser into the
     * Mockery if you want to mock classes.
     */
    public void setImposteriser(Imposteriser imposteriser) {
        this.imposteriser = imposteriser;
    }
    
    /**
     * Changes the naming scheme used to generate names for mock objects that 
     * have not been explicitly named in the test.
     * 
     * The default naming scheme names mock objects by lower-casing the first
     * letter of the class name, so a mock object of type BananaSplit will be
     * called "bananaSplit" if it is not explicitly named in the test.
     */
    public void setNamingScheme(MockObjectNamingScheme namingScheme) {
        this.namingScheme = namingScheme;
    }
    
    /**
     * Changes the expectation error translator used to translate expectation
     * errors into errors that report test failures.
     * 
     * By default, expectation errors are not translated and are thrown as
     * errors of type {@link ExpectationError}.  Plug in a new expectation error
     * translator if you want your favourite test framework to report expectation 
     * failures using its own error type.
     */
    public void setExpectationErrorTranslator(ExpectationErrorTranslator expectationErrorTranslator) {
        this.expectationErrorTranslator = expectationErrorTranslator;
    }
    
    /*
     * API
     */
    
    /**
     * Creates a mock object of type <var>typeToMock</var> and generates a name for it.
     */
    public <T> T mock(Class<T> typeToMock) {
		return mock(typeToMock, namingScheme.defaultNameFor(typeToMock));
	}
    
    /**
     * Creates a mock object of type <var>typeToMock</var> with the given name.
     */
    public <T> T mock(Class<T> typeToMock, String name) {
        MockObject mock = new MockObject(name);
        return imposteriser.imposterise(
            divert(Object.class, mock, divert(DispatcherControl.class, mock, mock)), 
            typeToMock, DispatcherControl.class);
    }
    
    private <T> Invokable divert(Class<T> type, T receiver, Invokable next) {
        return new InvocationDiverter<T>(type, receiver, next);
    }
    
    /**
     * Specifies the expected invocations that the object under test will perform upon
     * objects in its context during the test.
     * 
     * The builder is responsible for interpreting high-level, readable API calls to 
     * construct an expectation.
     */
	public void expects(ExpectationBuilder builder) {
        builder.setDefaultAction(defaultAction);
        expects(builder.toExpectation());
	}
	
    /**
     * Specifies the expected invocations that the object under test will perform upon
     * objects in its context during the test.
     * 
     * This method allows a test to define an expectation explicitly, bypassing the
     * high-level API, if desired.
     */
    public void expects(Expectation newExpectation) {
        expectation = newExpectation;
        capture = null;
    }
	
    /**
     * Fails if the test if there are any expectations that have not been met.
     */
	public void assertIsSatisfied() {
        firstError = null;
        if (expectation.needsMoreInvocations()) {
            throw expectationErrorTranslator.translate(new ExpectationError("not all expectations were satisfied", expectation));
        }
	}
    
    private Object dispatch(Invocation invocation) throws Throwable {
        if (isCapturingExpectations()) {
            capture.createExpectationFrom(invocation);
            return defaultAction.invoke(invocation);
        }
        else if (firstError != null) {
            throw firstError;
        }
        else {
            try {
                check(invocation);
                return expectation.invoke(invocation);
            }
            catch (ExpectationError e) {
                firstError = expectationErrorTranslator.translate(e);
                firstError.setStackTrace(e.getStackTrace());
                throw firstError;
            }
        }
    }
    
    private boolean isCapturingExpectations() {
        return capture != null;
    }
    
    private void check(Invocation invocation) {
        if (!expectation.matches(invocation)) {
            throw new ExpectationError("unexpected invocation", expectation, invocation);
        }
    }
    
    private class MockObject implements Invokable, DispatcherControl {
        private String name;
        
        public MockObject(String name) {
            this.name = name;
        }
        
        public String toString() {
            return name;
        }
        
        public Object invoke(Invocation invocation) throws Throwable {
            return dispatch(invocation);
        }
        
        public void startCapturingExpectations(ExpectationCapture newCapture) {
            capture = newCapture;
        }
        
        public void stopCapturingExpectations() {
            capture = null;
        }
    }
}

package com.consol.citrus.context;

public class XpathAssertionResult {

	private String actualResult;

	private String expectedResult;
	
	private String xpathExpression;
	
	public XpathAssertionResult(String xpathExpression, String expectedResult, String actualResult) {
		this.xpathExpression = xpathExpression;
		this.expectedResult = expectedResult;
		this.actualResult = actualResult;
	}

	public String getActualResult() {
		return this.actualResult;
	}

	public String getExpectedResult() {
		return this.expectedResult;
	}

	public String getXpathExpression() {
		return this.xpathExpression;
	}
	
	public String toString() {
		return "XPath assertion failure - " + this.xpathExpression + " : expected = " + this.expectedResult + ", actual = " + this.actualResult;
	}
	
}

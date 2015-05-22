package fractal.map.servlet.response;

import fractal.map.servlet.ServletError;

public abstract class AbstractInfo
{
	private boolean wasError = false;
	private String errorMessage = "";
	private int errorCode = 0;

	public boolean isWasError()
	{
		return wasError;
	}

	public void setWasError(boolean wasError)
	{
		this.wasError = wasError;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public int getErrorCode()
	{
		return errorCode;
	}

	public void setError(ServletError error)
	{
		errorCode = error.getErrorCode();
		errorMessage = error.getErrorMessage();
	}
}

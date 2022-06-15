package help;

public class Status {
    private boolean isSuccess;
    private String message;

    public Status(boolean isSuccess, String message)
    {
        this.isSuccess = isSuccess;
        this.message = message;
    }

    public boolean getIsSuccess()
    {
        return this.isSuccess;
    }

    public String getMessage()
    {
        return this.message;
    }
}

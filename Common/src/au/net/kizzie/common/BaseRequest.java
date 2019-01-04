package au.net.kizzie.common;

public class BaseRequest {
    protected final String givenRequest;
    protected final String[] parameters;
    
    public BaseRequest(String givenRequest) {
        this.givenRequest = givenRequest;
        this.parameters = givenRequest.split(" ");
    }
    
    public String getGivenRequest() {
        return givenRequest;
    }    
    
    /**
     * @return The number of parameters in the given request (space separated). 
     * Note that parameters[0] is the requestType string
     * So ...
     *   IsClear 2 3 
     * ... is 3 parameters
     */
    public int getNumParameters() {
        return parameters.length;   
    }
    
    /**
     * @param index The parameter to retrieve
     * @return The parameters with the given index (space separated). 
     * Note that parameters[0] is the requestType string.
     * Returns null if the index is out of range
     */
    public String getParameter(int index) {
        if (index >= 0 && parameters.length > index) {
            return parameters[index];
        } else {
            return null;
        }
    }
}

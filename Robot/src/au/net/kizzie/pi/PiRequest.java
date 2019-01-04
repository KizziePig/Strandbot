package au.net.kizzie.pi;

import au.net.kizzie.common.BaseRequest;

public class PiRequest extends BaseRequest {
    private PiRequestType requestType;
    
    public PiRequest(PiRequestType requestType, String givenRequest) {
        super(givenRequest);
        this.requestType = requestType;
    }
    
    public void setPiRequestType(PiRequestType requestType) {
        this.requestType = requestType;
    }
    
    public PiRequestType getPiRequestType() {
        return requestType;
    }
}

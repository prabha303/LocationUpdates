package in.vendor.rides.APIEngine;

import org.json.JSONObject;

/**
 * This controller handles the network operations.
 *
 * @author Jeevanandhan
 */

public interface ResponseListener {

    void successResponse(String successResponse, int flag);

    void successResponse(JSONObject jsonObject, int flag);

    void errorResponse(String errorResponse, int flag);

    void removeProgress(Boolean hideFlag);

}

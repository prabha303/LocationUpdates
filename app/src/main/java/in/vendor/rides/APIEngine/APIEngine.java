package in.vendor.rides.APIEngine;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.URL;


public class APIEngine {

    private static APIEngine apiEngine = null;

    public static APIEngine getInstance()
    {
        return apiEngine;

    }



   /* public static String validateUser(final Context context, final String userId, final String password)
    {
        new AsyncTask<Void, Void, String>()
        {
            protected String doInBackground(Void... params)
            {
                try
                {
                    String result = "";
                    String SOAP_ACTION = "http://btr-ltd.net/Webservice/ValidateUser";
                    String URL = "http://btr-ltd.net/Webservice/WebServicePartner.asmx";
                    String NAMESPACE = "http://btr-ltd.net/Webservice/";
                    String METHOD_NAME = "ValidateUser";
                    SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);
                    soapObject.addProperty("userName",userId);
                    soapObject.addProperty("password",password);
                    soapObject.addProperty("fcmid","ok");
                    SoapSerializationEnvelope envelope =  new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(soapObject);
                    HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
                    try {
                        httpTransportSE.call(SOAP_ACTION, envelope);
                        SoapPrimitive soapPrimitive = (SoapPrimitive)envelope.getResponse();
                        result = soapPrimitive.toString();
                        Log.d("result","--"+result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return result;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            }

        }.execute();
        return "";
    }*/


    private class DownloadFilesTask extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... params)
        {
            return "";
        }

        protected void onPostExecute(String  result) {

        }
    }





}

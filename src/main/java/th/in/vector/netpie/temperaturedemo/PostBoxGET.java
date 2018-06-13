package th.in.vector.netpie.temperaturedemo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

import th.in.vector.netpie.temperaturedemo.model.PostBoxModel;


/**
 * NetPIE demo
 * https://github.com/netpieio/microgear-restapi
 * @author VerifyFX (http://github.com/verifyfx)
 *
 */
public class PostBoxGET {

	//TODO: CHANGEME!
	private final static String APP_ID     = "YOUR_APP_ID";
	private final static String APP_KEY    = "YOUR_APP_KEY";
	private final static String APP_SECRET = "YOUR_APP_SECRET";
	private final static String POST_BOX_NAME  = "YOUR_MESSAGE_FOLDER";
	
	public final static String NETPIE_COLLECTORS_URL = "https://api.netpie.io/";
	
	private HttpClient client = null;
	private ScheduledExecutorService executor = null;
	
	public static void main(String[] args) {
		new PostBoxGET(); //bootstrap
	}
	
	public PostBoxGET() {
		//STEP 0: Prepare Event Timer Trigger So we can get the event every x seconds
		executor = Executors.newScheduledThreadPool(1);
		
		//STEP 1: Create Basic header for authentication
		byte[] credentials = Base64.getEncoder().encode((APP_KEY + ":" + APP_SECRET).getBytes(StandardCharsets.UTF_8));
		List<Header> headers = new ArrayList<>();
		headers.add(new BasicHeader("Authorization", "Basic " + new String(credentials, StandardCharsets.UTF_8)));
		
		//STEP 2: Create HttpClient
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(3000).build(); //set time out to 3 secs
		client = HttpClients.custom().setDefaultHeaders(headers).setDefaultRequestConfig(requestConfig).build();
		String postboxUrl = NETPIE_COLLECTORS_URL+"postbox/"+APP_ID+"/"+POST_BOX_NAME;
		
		//STEP 3: Read Events every 5 seconds
		executor.scheduleAtFixedRate(() -> {
			try {
				HttpUriRequest request = RequestBuilder.get().setUri(postboxUrl).build();
				HttpResponse response = client.execute(request);
				String responseBody = EntityUtils.toString(response.getEntity());
				if (responseBody != null) { //If response is readable
					PostBoxModel[] data = new Gson().fromJson(responseBody, PostBoxModel[].class);
					for(int i = 0 ; i < data.length ; i++) {
						System.out.println("read temperature = " + data[i].getMsg());
					}
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}, 0, 500, TimeUnit.MILLISECONDS);
		
		//STEP 4: WAIT Forever
		while(true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.exit(0);
			}
		}
	}
	
	public int getTemperature() {
		int min = -20;
		int max = 120;
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}
}


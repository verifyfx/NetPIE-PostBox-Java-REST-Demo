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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;


/**
 * NetPIE demo
 * https://github.com/netpieio/microgear-restapi
 * @author VerifyFX (http://github.com/verifyfx)
 *
 */
public class PostBoxPUT {
	
	//TODO: CHANGEME!
	private final static String APP_ID     = "YOUR_APP_ID";
	private final static String APP_KEY    = "YOUR_APP_KEY";
	private final static String APP_SECRET = "YOUR_APP_SECRET";
	private final static String POST_BOX_NAME  = "YOUR_MESSAGE_FOLDER";
	
	public final static String NETPIE_COLLECTORS_URL = "https://api.netpie.io/";
	
	private HttpClient client = null;
	private ScheduledExecutorService executor = null;
	
	public static void main(String[] args) {
		new PostBoxPUT(); //bootstrap
	}
	
	public PostBoxPUT() {
		//STEP 0: Prepare Event Timer Trigger So we can send the event every x seconds
		executor = Executors.newScheduledThreadPool(1);
		
		//STEP 1: Create Basic header for authentication
		byte[] credentials = Base64.getEncoder().encode((APP_KEY + ":" + APP_SECRET).getBytes(StandardCharsets.UTF_8));
		List<Header> headers = new ArrayList<>();
		headers.add(new BasicHeader("Authorization", "Basic " + new String(credentials, StandardCharsets.UTF_8)));
		
		//STEP 2: Create HttpClient
		client = HttpClients.custom().setDefaultHeaders(headers).build();
		String postboxUrl = NETPIE_COLLECTORS_URL+"postbox/"+APP_ID+"/"+POST_BOX_NAME;
		
		//STEP 3: Sends Events every 1 seconds
		executor.scheduleAtFixedRate(() -> {
			try {
				String message = String.format("%d", getTemperature());
				StringEntity body = new StringEntity(message);
				HttpUriRequest request = RequestBuilder.put().setUri(postboxUrl).setEntity(body).build();
				System.out.println("sent temperature = " + message);
				HttpResponse response = client.execute(request);
				@SuppressWarnings("unused")
				String responseBody = EntityUtils.toString(response.getEntity());
//				System.out.print("Server response: " + responseBody);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}, 0, 1, TimeUnit.SECONDS);
		
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

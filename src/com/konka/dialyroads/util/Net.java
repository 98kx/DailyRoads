package com.konka.dialyroads.util;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;

import com.konka.dialyroads.myinterface.CallBackSize;
import com.konka.dialyroads.pojo.ImageFileBean;
import com.konka.dialyroads.pojo.Registerpare;
import com.konka.dialyroads.pojo.User;
import com.konka.dialyroads.pojo.VideoFileBean;
import com.konka.dialyroads.pojo.WebPicFileInfo;
import com.konka.dialyroads.pojo.WebVideoFileInfo;

public class Net {
	public static String upload_url = "http://" + Assist.domainName + "/post/uploadfile";// 图片上传地址
	public static String login_url = "http://" + Assist.domainName + "/post/login"; // 登陆地址
	public static String downdata_url = "http://" + Assist.domainName + "/post/createXML"; // 获取网络图片列表
	public static String video_download_url = "http://" + Assist.domainName + "/video/createXML";
	public static File sd_file = new File(Environment.getExternalStorageDirectory(), "konka/download");

	public static void init() {
		upload_url = "http://" + Assist.domainName + "/post/uploadfile";// 图片上传地址
		login_url = "http://" + Assist.domainName + "/post/login"; // 登陆地址
		downdata_url = "http://" + Assist.domainName + "/post/createXML"; // 获取网络图片列表
		video_download_url = "http://" + Assist.domainName + "/video/createXML";
	}

	/**
	 * 登录
	 * 
	 * @param username
	 *            用户名
	 * @param password
	 *            密码（已经经过md5加密后的）
	 * @return
	 */
	public static boolean login(String username, String password) {
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		HttpPost httppost = new HttpPost(login_url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		try {
			HttpEntity httpEntity = new UrlEncodedFormEntity(params);
			httppost.setEntity(httpEntity);
			HttpResponse httpResponse = httpClient.execute(httppost);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = httpResponse.getEntity();
				String result = EntityUtils.toString(entity);
				System.out.println(result);
				if ("success".equals(result)) {

					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 注册
	 * 
	 * @param registerpare
	 *            封装表单信息
	 */
	public static boolean register(Registerpare registerpare) {
		// HttpClient httpClient = new DefaultHttpClient();
		// httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
		// HttpVersion.HTTP_1_1);
		System.out.println("1111");
		HttpPost httppost = new HttpPost("http://115.28.57.151:83/site/login");
		System.out.println("2222");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("Register[name]", registerpare.getUsername()));
		params.add(new BasicNameValuePair("Register[password]", registerpare.getPassword()));
		params.add(new BasicNameValuePair("Register[passwordConfirm]", registerpare.getPasswordConfirm()));
		params.add(new BasicNameValuePair("Register[email]", registerpare.getEmail()));
		params.add(new BasicNameValuePair("Register[verifyCode]", registerpare.getCode()));
		try {
			System.out.println("3333");
			HttpEntity httpEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
			httppost.setEntity(httpEntity);
			System.out.println("httpEntity.toString()--" + httpEntity.getContentType().getName());
			HttpResponse httpResponse = httpClient.execute(httppost);

			System.out.println("44444");
			System.out.println("44444" + httpResponse.getStatusLine().getStatusCode());
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = httpResponse.getEntity();
				String result = EntityUtils.toString(entity, HTTP.UTF_8);
				// System.out.println(result);
				if (result.contains("<title>图片</title>")) {
					System.out.println("注册成功");
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return false;
	}

	/**
	 * 上传
	 * 
	 * @param file
	 * @param username
	 * @param password
	 * @return
	 */
	public static boolean upload(File file, String username, String password) {
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		HttpPost httppost = new HttpPost(upload_url);
		try {
			// MultipartEntity mpEntity = new MultipartEntity();
			MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.forName(HTTP.UTF_8));
			// 此处可以将获取的类型上传 如
			mpEntity.addPart("userfile", new FileBody(file, ""));
			mpEntity.addPart("username", new StringBody(username));
			mpEntity.addPart("password", new StringBody(password));
			mpEntity.addPart("type", new StringBody("3"));
			httppost.setEntity(mpEntity); // 传递file
			HttpResponse response = httpClient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				String result = EntityUtils.toString(resEntity);
				httpClient.getConnectionManager().shutdown();
				System.out.println(result);
				if ("success".equals(result)) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 上传图片文件
	 * 
	 * @param file
	 * @param username
	 * @param password
	 * @return
	 */
	public static boolean uploadImageFile(ImageFileBean imageFileBean, String username, String password) {
		password = MD5.getMD5String(password);
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		HttpPost httppost = new HttpPost(upload_url);
		try {
			// MultipartEntity mpEntity = new MultipartEntity();
			// MultipartEntity mpEntity = new
			// MyMultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,
			// null, Charset.forName(HTTP.UTF_8)); // 解决中文乱码问题
			// ContentBody cbFile = new FileBody(file, ""); // 此处可以将获取的类型上传 如
			MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.forName(HTTP.UTF_8));
			// 此处可以将获取的类型上传 如

			mpEntity.addPart("userfile", new FileBody(new File(imageFileBean.getPath()), ""));
			mpEntity.addPart("username", new StringBody(username));
			mpEntity.addPart("password", new StringBody(password));
			mpEntity.addPart("type", new StringBody("1"));

			httppost.setEntity(mpEntity); // 传递file
			HttpResponse response = httpClient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				String result = EntityUtils.toString(resEntity);
				httpClient.getConnectionManager().shutdown();
				System.out.println(result);
				if ("success".equals(result)) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 上传视频文件
	 * 
	 * @param file
	 * @param username
	 * @param password
	 * @return
	 */
	public static boolean uploadVideoFile(VideoFileBean VideoFileBean, User user, MultipartEntity mpEntity) {
		// password = MD5.getMD5String(password);
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		HttpPost httppost = new HttpPost(upload_url);
		try {
			mpEntity.addPart("userfile", new FileBody(new File(VideoFileBean.getPath()), ""));
			mpEntity.addPart("username", new StringBody(user.getUsername()));
			mpEntity.addPart("password", new StringBody(MD5.getMD5String(user.getPassword())));
			mpEntity.addPart("type", new StringBody("0"));

			httppost.setEntity(mpEntity); // 传递file
			HttpResponse response = httpClient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				String result = EntityUtils.toString(resEntity);
				httpClient.getConnectionManager().shutdown();
				System.out.println(result);
				if ("success".equals(result)) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @param path
	 *            文件路径
	 * @param fileType
	 *            文件类型 图片还是视频
	 * @param user
	 *            用户
	 * @param mpEntity
	 *            封装参数
	 * @return
	 */
	public static boolean uploadFile(String path, int fileType, User user, MultipartEntity mpEntity) {
		// password = MD5.getMD5String(password);
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		HttpPost httppost = new HttpPost(upload_url);
		try {
			mpEntity.addPart("userfile", new FileBody(new File(path), ""));
			mpEntity.addPart("username", new StringBody(user.getUsername()));
			mpEntity.addPart("password", new StringBody(MD5.getMD5String(user.getPassword())));
			mpEntity.addPart("type", new StringBody(fileType + ""));

			httppost.setEntity(mpEntity); // 传递file
			HttpResponse response = httpClient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				String result = EntityUtils.toString(resEntity);
				httpClient.getConnectionManager().shutdown();
				System.out.println(result);
				if ("success".equals(result)) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取网络图片信息
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public static List<WebPicFileInfo> getpicdata(String username, String password) {
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		HttpPost httppost = new HttpPost(downdata_url);
		try {
			System.out.println(555555);
			// MultipartEntity mpEntity = new MultipartEntity();
			MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.forName(HTTP.UTF_8));
			mpEntity.addPart("username", new StringBody(username));
			mpEntity.addPart("password", new StringBody(password));
			System.out.println(66666);
			httppost.setEntity(mpEntity);
			HttpResponse response = httpClient.execute(httppost);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {
				// String result = EntityUtils.toString(resEntity,HTTP.UTF_8);
				InputStream inputStream = resEntity.getContent();
				// System.out.println(result);

				List<WebPicFileInfo> fileInfos = xml2FileInfos(inputStream);
				return fileInfos;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<WebPicFileInfo>();
	}

	public static List<WebVideoFileInfo> getvideoinfo(String username, String password) {
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		HttpPost httppost = new HttpPost(video_download_url);
		try {
			MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, Charset.forName(HTTP.UTF_8));
			mpEntity.addPart("username", new StringBody(username));
			mpEntity.addPart("password", new StringBody(password));
			httppost.setEntity(mpEntity);
			HttpResponse response = httpClient.execute(httppost);
			HttpEntity resEntity = response.getEntity();
			if (resEntity != null) {
				InputStream inputStream = resEntity.getContent();
				List<WebVideoFileInfo> fileInfos = xml2VideoFileInfos(inputStream);
				return fileInfos;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<WebVideoFileInfo>();
	}

	/**
	 * 下载图片信息
	 * 
	 * @param inputStream
	 * @return
	 */
	public static List<WebPicFileInfo> xml2FileInfos(InputStream inputStream) {
		XmlPullParser xpp;
		WebPicFileInfo fileInfo = null;
		List<WebPicFileInfo> fileInfos = new ArrayList<WebPicFileInfo>();
		try {
			xpp = XmlPullParserFactory.newInstance().newPullParser();
			xpp.setInput(inputStream, HTTP.UTF_8);
			// xpp.setInput(inputStream, "UTF-8");
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_DOCUMENT) {// 开始
					// 暂时不需要处理
					System.out.println("Start document");
				} else if (eventType == XmlPullParser.START_TAG) {// 标签开始
					if ("data".equals(xpp.getName())) {
						fileInfo = new WebPicFileInfo();
					} else if ("id".equals(xpp.getName())) {
						int id = Integer.parseInt(xpp.nextText());
						System.out.println("id--" + id);
						fileInfo.setId(id);
					} else if ("create_time".equals(xpp.getName())) {
						String create_time = xpp.nextText();// .replaceAll("[?\\/<>|:*\"]","");
						System.out.println("标题" + create_time);
						fileInfo.setCreate_time(create_time);

					} else if ("urls".equals(xpp.getName())) {
						String urlssString = xpp.nextText();
						String[] urlarray = urlssString.split(",");
						List<String> urls = Arrays.asList(urlarray);
						fileInfo.setLists(urls);
					}
					System.out.println("Start tag " + xpp.getName());
				} else if (eventType == XmlPullParser.END_TAG) {// 标签结束
					if ("data".equals(xpp.getName())) {
						if (fileInfo != null) {
							fileInfos.add(fileInfo);
						}
					}
					System.out.println("End tag " + xpp.getName());
				} else if (eventType == XmlPullParser.TEXT) {// 文本
					// 这里不可能出现了，上面已经处理了
					System.out.println("Text " + xpp.getText());
				}
				eventType = xpp.next();
			}
			return fileInfos;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileInfos;
	}

	/**
	 * 下载视频信息
	 * 
	 * @param inputStream
	 * @return
	 */
	public static List<WebVideoFileInfo> xml2VideoFileInfos(InputStream inputStream) {
		XmlPullParser xpp;
		WebVideoFileInfo fileInfo = null;
		List<WebVideoFileInfo> fileInfos = new ArrayList<WebVideoFileInfo>();
		try {
			xpp = XmlPullParserFactory.newInstance().newPullParser();
			xpp.setInput(inputStream, HTTP.UTF_8);
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_DOCUMENT) {// 开始
					// 暂时不需要处理
					System.out.println("Start document");
				} else if (eventType == XmlPullParser.START_TAG) {// 标签开始
					if ("data".equals(xpp.getName())) {
						fileInfo = new WebVideoFileInfo();
					} else if ("id".equals(xpp.getName())) {
						int id = Integer.parseInt(xpp.nextText());
						System.out.println("id--" + id);
						fileInfo.setId(id);
					} else if ("title".equals(xpp.getName())) {
						String title = xpp.nextText();// .replaceAll("[?\\/<>|:*\"]","");
						System.out.println("标题" + title);
						fileInfo.setTitle(title);

					} else if ("url".equals(xpp.getName())) {
						String url = xpp.nextText();
						fileInfo.setUrl(url);
					} else if ("thumbnail".equals(xpp.getName())) {
						String thumbnail = xpp.nextText();
						fileInfo.setThumbnail(thumbnail);
					}
				} else if (eventType == XmlPullParser.END_TAG) {// 标签结束
					if ("data".equals(xpp.getName())) {
						if (fileInfo != null) {
							fileInfos.add(fileInfo);
						}
					}
					System.out.println("End tag " + xpp.getName());
				} else if (eventType == XmlPullParser.TEXT) {// 文本
					// 这里不可能出现了，上面已经处理了
					System.out.println("Text " + xpp.getText());
				}
				eventType = xpp.next();
			}
			return fileInfos;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileInfos;
	}

	public static File downFile(String url, String filename) {
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream inputStream = httpResponse.getEntity().getContent();
				// File file=new File(sd_file, Math.random()+"");
				File file = new File(sd_file, filename);
				FileUtils.copyInputStreamToFile(inputStream, file);
				return file;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static DefaultHttpClient httpClient;

	public static InputStream getVerificationCode() {
		String url = "http://115.28.57.151:83/site/captcha/refresh/1?_=" + System.currentTimeMillis();
		httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse httpResponse = httpClient.execute(httpGet);

			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = httpResponse.getEntity();
				String result = EntityUtils.toString(entity, HTTP.UTF_8);
				JSONObject jsonObject = new JSONObject(result);
				String string = (String) jsonObject.get("url");
				HttpGet httpGet2 = new HttpGet("http://" + Assist.domainName + string);
				HttpResponse httpResponse2 = httpClient.execute(httpGet2);
				if (httpResponse2.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					HttpEntity entity2 = httpResponse2.getEntity();
					InputStream is = entity2.getContent();
					return is;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
}

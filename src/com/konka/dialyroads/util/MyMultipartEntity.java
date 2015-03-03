package com.konka.dialyroads.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

import com.konka.dialyroads.myinterface.CallBackSize;

public class MyMultipartEntity extends MultipartEntity {
	private int alreadyUploadFinishSize;
	private CallBackSize callBack;

	public MyMultipartEntity(HttpMultipartMode arg0, String arg1, Charset arg2) {
		super(arg0, arg1, arg2);
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		super.writeTo(new MyFilterOutputStream(outstream));
	}

	void setCallBack(CallBackSize callBack) {
		this.callBack = callBack;
		
	}

	void doSth() {
		if(callBack!=null){
			callBack.callbackSize(alreadyUploadFinishSize);
		}
	}

	class MyFilterOutputStream extends FilterOutputStream {

		public MyFilterOutputStream(OutputStream out) {
			super(out);
		}

		@Override
		public void write(byte[] buffer, int offset, int length) throws IOException {

			out.write(buffer, offset, length);
			System.out.println(alreadyUploadFinishSize);
			alreadyUploadFinishSize += length;
			doSth();
		}
		
		@Override
		public void write(int oneByte) throws IOException {
			out.write(oneByte);
			alreadyUploadFinishSize += oneByte;
			doSth();
		}
	}
}

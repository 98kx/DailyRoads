package com.konka.dialyroads.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.konka.dialyroads.myinterface.CallBackSize;

public class MyFilterOutputStream extends FilterOutputStream {

	int alreadyUploadFinishSize;
	CallBackSize callBack;
	public MyFilterOutputStream(OutputStream out) {
		super(out);
	}
	@Override
	public void write(byte[] buffer, int offset, int length) throws IOException {

		out.write(buffer, offset, length);
		alreadyUploadFinishSize += length;
	}
	@Override
	public void write(int oneByte) throws IOException {
		out.write(oneByte);
		alreadyUploadFinishSize += oneByte;
	}

	public void setCallBack(CallBackSize callBack) {
		this.callBack = callBack;
	}
	
	public void doSth() {
		callBack.callbackSize(alreadyUploadFinishSize);
	}
}

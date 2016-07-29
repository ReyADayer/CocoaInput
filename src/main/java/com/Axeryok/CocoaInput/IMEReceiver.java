package com.Axeryok.CocoaInput;

import com.Axeryok.CocoaInput.impl.IMEOperator;

public interface IMEReceiver{
	String getUUID();
	void insertText(String aString,int position1,int length1);//確定文字列 現状aString以外の引数は意味をなしてない
	/*
	 * position1 length1は下線と強調変換のため必須 position2 length2は意味をなしてない
	 * positionの位置から文字数lengthの範囲という意味
	 */
	void setMarkedText(String aString,int position1,int length1,int position2,int length2);//
}

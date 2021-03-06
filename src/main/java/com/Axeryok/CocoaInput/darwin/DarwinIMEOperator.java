package com.Axeryok.CocoaInput.darwin;

import com.Axeryok.CocoaInput.IMEReceiver;
import com.Axeryok.CocoaInput.ModLogger;
import com.Axeryok.CocoaInput.darwin.CallbackFunction.*;
import com.Axeryok.CocoaInput.impl.IMEOperator;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;

import net.minecraft.client.gui.GuiTextField;

public class DarwinIMEOperator implements IMEOperator{
	IMEReceiver owner;
	Func_insertText insertText_p;
	Func_setMarkedText setMarkedText_p;
	Func_firstRectForCharacterRange firstRectForCharacterRange_p;
	public DarwinIMEOperator(IMEReceiver field){
		this.owner=field;
		insertText_p=new Func_insertText(){
			@Override
			public void invoke(String str, int position, int length) {
				ModLogger.debug(3, "Textfield "+owner.getUUID()+" received inserted text.");
				owner.insertText(str, position, length);
			}
		};
		setMarkedText_p=new Func_setMarkedText(){

			@Override
			public void invoke(String str, int position1, int length1,
					int position2, int length2) {
				ModLogger.debug(3, "MarkedText changed at "+owner.getUUID()+".");
				owner.setMarkedText(str, position1, length1, position2, length2);;
			}
			
		};
		firstRectForCharacterRange_p=new Func_firstRectForCharacterRange(){

			@Override
			public Pointer invoke() {
				ModLogger.debug(3, "Called to determine where to draw.");
				float []point={
			            org.lwjgl.opengl.Display.getX(),
			            Handle.INSTANCE.invertYCoordinate(org.lwjgl.opengl.Display.getY()),
			            0,
			            0
			        };//TODO 描画位置改善
				if(owner instanceof GuiTextField){
					GuiTextField textField=(GuiTextField) owner;
					float x = org.lwjgl.opengl.Display.getX()
							+ (textField.fontRendererInstance.getStringWidth(textField.getText().substring(0, textField.cursorPosition)) * 2
									+ (textField.enableBackgroundDrawing ? textField.xPosition + 4 : textField.xPosition) * 2);
					float y = org.lwjgl.opengl.Display.getY()
							+ (textField.enableBackgroundDrawing ? textField.yPosition + (textField.height - 8) / 2 : textField.yPosition) * 2
							+ textField.fontRendererInstance.FONT_HEIGHT * 2;
					point[0]=x;
					point[1]=Handle.INSTANCE.invertYCoordinate(y);
					point[2]=textField.width;
					point[3]=textField.height;
				}
				Pointer ret=new Memory(Float.BYTES*4);
				ret.write(0,point,0,4);
				return ret;
			}
			
		};
		Handle.INSTANCE.addInstance(owner.getUUID(), insertText_p, setMarkedText_p, firstRectForCharacterRange_p);
	}
	
	public void discardMarkedText(){
		Handle.INSTANCE.discardMarkedText(owner.getUUID());
	}
	
	public void removeInstance(){
		Handle.INSTANCE.removeInstance(owner.getUUID());
	}
	
	public void setFocused(boolean yn){
		Handle.INSTANCE.setIfReceiveEvent(owner.getUUID(), yn==true?1:0);
	}
	
}

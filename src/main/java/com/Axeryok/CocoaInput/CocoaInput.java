package com.Axeryok.CocoaInput;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.Display;

import com.Axeryok.CocoaInput.asm.CocoaInputTransformer;
import com.Axeryok.CocoaInput.darwin.CallbackFunction;
import com.Axeryok.CocoaInput.darwin.DarwinController;
import com.Axeryok.CocoaInput.darwin.Handle;
import com.Axeryok.CocoaInput.impl.Controller;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.sun.jna.Platform;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CocoaInput extends DummyModContainer
{
    public static final String MODID = "CocoaInput";
    public static final String VERSION = "3.0.7";
    public static Configuration configFile;
    public static Controller instance;
    public static boolean isActive=false;
    public CocoaInput(){
    	super(new ModMetadata());
    	ModMetadata meta = getMetadata();
    	meta.modId=MODID;
    	meta.name="CocoaInput";
    	meta.description="Support IME input on OSX.";
    	meta.version=this.VERSION;
    	meta.authorList=Arrays.asList("Axer");
    	meta.credits="Logo was painted by RedWheat.This mod uses JavaNativeAccess(Apache License2).";
    	meta.logoFile="/logo.png";
    	this.setEnabledState(true);
    	
    }
    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
    	bus.register(this);
        return true;
    }
    @Override
    public String getGuiClassName(){
    	return "com.Axeryok.CocoaInput.gui.CocoaInputGuiFactory";
    }
    
    @Subscribe
    public void preInit(FMLPreInitializationEvent event){
    	configFile = new Configuration(event.getSuggestedConfigurationFile());
    	this.syncConfig();
    }
    
    @Subscribe
    public void init(FMLInitializationEvent event) throws Exception
    {
    	if(Platform.isMac()){
    		isActive=true;
    		ModLogger.debug(0, "CocoaInput has loaded Controller:"+DarwinController.class.toString());
    		this.instance=new DarwinController();
    	}
    	else{
    		ModLogger.error("There are no available Controller.");
    		return;
    	}
    	this.copyLibrary();
    	this.instance.CocoaInputInitialization(event);
    	MinecraftForge.EVENT_BUS.register(this);
    }
    
    
    
    //TextFormatting.getTextWithoutFormattingCodes(String str)の代替
  	public static String returnSameObject(String obj){
  		return obj;
  	}
    
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event){
    	if(event.getModID().equals("CocoaInput")){
    		this.syncConfig();
    		ModLogger.log("Configuration has changed.");
    	}
    }
    
    private void syncConfig(){
    	ModLogger.debugLevel=configFile.getInt("debugLevel", Configuration.CATEGORY_GENERAL, 0, 0, 4, "Logger shows debug messages less than the debugLevel you set.");
    	if(configFile.hasChanged()){
    		configFile.save();
    	}
    }
    
    private void copyLibrary() throws IOException{
    	if(this.instance.getLibraryName()==null)return;
    	InputStream libFile=this.getClass().getResourceAsStream(this.instance.getLibraryPath());
		File nativeDir=new File(Minecraft.getMinecraft().mcDataDir.getAbsolutePath().concat("/native"));
		File copyLibFile=new File(Minecraft.getMinecraft().mcDataDir.getAbsolutePath().concat("/native/"+this.instance.getLibraryName()));
		try {
			nativeDir.mkdir();
			FileOutputStream fos=new FileOutputStream(copyLibFile);
			copyLibFile.createNewFile();
			IOUtils.copy(libFile, fos);
			fos.close();
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			ModLogger.error("Attempted to copy library to ./native/"+this.instance.getLibraryName()+" but failed.");
			throw e1;
		}
		System.setProperty("jna.library.path",nativeDir.getAbsolutePath());
    }
    
    public static String formatMarkedText(String aString,int position1,int length1){
		StringBuilder builder=new StringBuilder(aString);
		if(length1!=0){
			builder.insert(position1+length1, "§r§n");
			builder.insert(position1,"§l");
		}
		builder.insert(0, "§n");
		builder.append("§r");
		
		return new String(builder);
	}
    
    
}

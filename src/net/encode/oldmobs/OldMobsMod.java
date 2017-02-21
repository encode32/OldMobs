package net.encode.oldmobs;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmClientMod;

import com.wurmonline.client.comm.ServerConnectionListenerClass;
import com.wurmonline.client.options.Options;
import com.wurmonline.client.renderer.cell.CreatureCellRenderable;
import com.wurmonline.client.renderer.cell.MobileModelRenderable;

public class OldMobsMod implements WurmClientMod, Initable, PreInitable, Configurable{
	public Logger logger = Logger.getLogger("OldMobsMod");
	
	float[] colorAlert = {0.0f, 0.0f, 0.0f};
	float[] colorAngry = {0.0f, 0.0f, 0.0f};
	float[] colorChampion = {0.0f, 0.0f, 0.0f};
	float[] colorDiseased = {0.0f, 0.0f, 0.0f};
	float[] colorFierce = {0.0f, 0.0f, 0.0f};
	float[] colorGreenish = {0.0f, 0.0f, 0.0f};
	float[] colorHardened = {0.0f, 0.0f, 0.0f};
	float[] colorLurking = {0.0f, 0.0f, 0.0f};
	float[] colorRaging = {0.0f, 0.0f, 0.0f};
	float[] colorScared = {0.0f, 0.0f, 0.0f};
	float[] colorSlow = {0.0f, 0.0f, 0.0f};
	float[] colorSly = {0.0f, 0.0f, 0.0f};
	
	private float[] colorStringToFloatA(String color)
	{
		String[] colors = color.split(",");
		float[] colorf = {
				Float.valueOf(colors[0])/255.0f,
				Float.valueOf(colors[1])/255.0f,
				Float.valueOf(colors[2])/255.0f};
		return colorf;
	}
	
	private String colorFloatAToString(float[] color)
	{
		String colors = 
				String.valueOf(color[0]*255.0f) + "," +
				String.valueOf(color[1]*255.0f) + "," +
				String.valueOf(color[2]*255.0f);
		return colors;
	}
	
	@Override
	public void configure(Properties properties) {
		colorAlert = colorStringToFloatA(
				properties.getProperty("colorAlert", 
						colorFloatAToString(colorAlert)));
		colorAngry = colorStringToFloatA(
				properties.getProperty("colorAngry", 
						colorFloatAToString(colorAngry)));
		colorChampion = colorStringToFloatA(
				properties.getProperty("colorChampion", 
						colorFloatAToString(colorChampion)));
		colorDiseased = colorStringToFloatA(
				properties.getProperty("colorDiseased", 
						colorFloatAToString(colorDiseased)));
		colorFierce = colorStringToFloatA(
				properties.getProperty("colorFierce", 
						colorFloatAToString(colorFierce)));
		colorGreenish = colorStringToFloatA(
				properties.getProperty("colorGreenish", 
						colorFloatAToString(colorGreenish)));
		colorHardened = colorStringToFloatA(
				properties.getProperty("colorHardened", 
						colorFloatAToString(colorHardened)));
		colorLurking = colorStringToFloatA(
				properties.getProperty("colorLurking", 
						colorFloatAToString(colorLurking)));
		colorRaging = colorStringToFloatA(
				properties.getProperty("colorRaging", 
						colorFloatAToString(colorRaging)));
		colorScared = colorStringToFloatA(
				properties.getProperty("colorScared", 
						colorFloatAToString(colorScared)));
		colorSlow = colorStringToFloatA(
				properties.getProperty("colorSlow", 
						colorFloatAToString(colorSlow)));
		colorSly = colorStringToFloatA(
				properties.getProperty("colorSly", 
						colorFloatAToString(colorSly)));
	}
		
	private float[] chooseColor(String hoverName, float[] color)
	{
		if(hoverName.contains("alert"))
		{
			color = colorAlert;
		}else if(hoverName.contains("angry"))
		{
			color = colorAngry;
		}else if(hoverName.contains("champion"))
		{
			color = colorChampion;
		}else if(hoverName.contains("diseased"))
		{
			color = colorDiseased;
		}else if(hoverName.contains("fierce"))
		{
			color = colorFierce;
		}else if(hoverName.contains("greenish"))
		{
			color = colorGreenish;
		}else if(hoverName.contains("hardened"))
		{
			color = colorHardened;
		}else if(hoverName.contains("lurking"))
		{
			color = colorLurking;
		}else if(hoverName.contains("raging"))
		{
			color = colorRaging;
		}else if(hoverName.contains("scared"))
		{
			color = colorScared;
		}else if(hoverName.contains("slow"))
		{
			color = colorSlow;
		}else if(hoverName.contains("sly"))
		{
			color = colorSly;
		}else
		{
			return null;
		}
		return color;
	}
	
	private Object[] setArgs(Object[] args, float[] color)
	{
		args[1] = color[0];
		args[2] = color[1];
		args[3] = color[2];
		args[4] = 1.0f;
		args[5] = 0;
		
		return args;
	}
	
	@Override
	public void preInit() {}
	
	@Override
	public void init() {
		logger.fine("Initializing");

		try {
			HookManager.getInstance().registerHook("com.wurmonline.client.comm.ServerConnectionListenerClass",
					"repaint", "(JFFFFI)V", () -> (proxy, method, args) -> {
						ServerConnectionListenerClass serCon = (ServerConnectionListenerClass)proxy;
						
						long id = (long) args[0];
						float r = (float) args[1];
						float g = (float) args[2];
						float b = (float) args[3];
						
						CreatureCellRenderable creature = serCon.getCreatures().get(id);
				        
				        if (creature != null) {
				        	String hoverName = creature.getHoverName();
							float[] color = {r, g, b};
				        	
							color = this.chooseColor(hoverName, color);
							
				        	if(color != null)
							{
								args = setArgs(args,color);
							}
				        } else if (Options.logExtraErrors.value()) {
				            logger.warning("Can't (re)paint creature " + id + " because it doesn't exist");
				        }
				        
				        method.invoke(proxy, args);
				        
						return null;
					});
			
			HookManager.getInstance().registerHook("com.wurmonline.client.renderer.cell.MobileModelRenderable",
					"initialize", "()V", () -> (proxy, method, args) -> {
						method.invoke(proxy, args);
						MobileModelRenderable mob = (MobileModelRenderable)proxy;
						
						String hoverName = mob.getHoverName();
						float[] color = {0.0f, 0.0f, 0.0f};
						
						color = this.chooseColor(hoverName, color);
						
						if(color != null)
						{
							mob.setPaint(color[0], color[1], color[2], 1.0f, 0);
						}
						
						return null;
					});
					
			logger.fine("Loaded");
		} catch (Throwable e) {
			logger.log(Level.SEVERE, "Error loading mod", e);
		}
	}

}

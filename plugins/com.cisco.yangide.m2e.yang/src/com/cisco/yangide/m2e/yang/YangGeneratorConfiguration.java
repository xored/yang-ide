package com.cisco.yangide.m2e.yang;

import java.io.File;
import java.util.Map;

/**
 * @author Konstantin Zaitsev
 */
public class YangGeneratorConfiguration {
	private String codeGeneratorClass;
	private File outputBaseDir;
	private File resourceBaseDir;
	private Map<String, String> additionalConfiguration;
	
	/**
	 * @return the codeGeneratorClass
	 */
	public String getCodeGeneratorClass() {
		return codeGeneratorClass;
	}
	/**
	 * @param codeGeneratorClass the codeGeneratorClass to set
	 */
	public void setCodeGeneratorClass(String codeGeneratorClass) {
		this.codeGeneratorClass = codeGeneratorClass;
	}
	/**
	 * @return the outputBaseDir
	 */
	public File getOutputBaseDir() {
		return outputBaseDir;
	}
	/**
	 * @param outputBaseDir the outputBaseDir to set
	 */
	public void setOutputBaseDir(File outputBaseDir) {
		this.outputBaseDir = outputBaseDir;
	}
	/**
	 * @return the resourceBaseDir
	 */
	public File getResourceBaseDir() {
		return resourceBaseDir;
	}
	/**
	 * @param resourceBaseDir the resourceBaseDir to set
	 */
	public void setResourceBaseDir(File resourceBaseDir) {
		this.resourceBaseDir = resourceBaseDir;
	}
	/**
	 * @return the additionalConfiguration
	 */
	public Map<String, String> getAdditionalConfiguration() {
		return additionalConfiguration;
	}
	/**
	 * @param additionalConfiguration the additionalConfiguration to set
	 */
	public void setAdditionalConfiguration(
			Map<String, String> additionalConfiguration) {
		this.additionalConfiguration = additionalConfiguration;
	}
}

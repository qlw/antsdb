/*-------------------------------------------------------------------------------------------------
 _______ __   _ _______ _______ ______  ______
 |_____| | \  |    |    |______ |     \ |_____]
 |     | |  \_|    |    ______| |_____/ |_____]

 Copyright (c) 2016, antsdb.com and/or its affiliates. All rights reserved. *-xguo0<@

 This program is free software: you can redistribute it and/or modify it under the terms of the
 GNU Affero General Public License, version 3, as published by the Free Software Foundation.

 You should have received a copy of the GNU Affero General Public License along with this program.
 If not, see <https://www.gnu.org/licenses/agpl-3.0.txt>
-------------------------------------------------------------------------------------------------*/
package com.antsdb.saltedfish.nosql;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.antsdb.saltedfish.util.SizeConstants;

/**
 * 
 * @author wgu0
 */
public class ConfigService {
    static final int KB = 1024;
    static final int MB = 1024 * KB;
    static final int GB = 1024 * MB;
    
    Properties props;
    File file;

    public ConfigService() {
        this.props = new Properties();
    }
    
    public ConfigService(File file) throws Exception {
    	this.file = file;
		this.props = new Properties();
		if (file.exists()) {
			try (FileInputStream in = new FileInputStream(file)) {
				props.load(in);
			}
		}
	}

    public boolean isHbaseOn() {
        return getStorageEngineName().equals("hbase");
    }

	public boolean isValidationOn() {
    	String value = this.props.getProperty("humpback.data.validation", "false");
    	try {
    		return Boolean.parseBoolean(value);
    	}
    	catch (Exception x) {}
    	return false;
	}

	public boolean isLogWriterEnabled() {
    	String value = this.props.getProperty("humpback.log.writer", "true");
    	try {
    		return Boolean.parseBoolean(value);
    	}
    	catch (Exception x) {}
    	return true;
	}

	public int getSpaceFileSize() {
    	String value = this.props.getProperty("humpback.space.file.size", "256");
    	try {
    		return Integer.parseInt(value) * 1024 * 1024;
    	}
    	catch (Exception x) {}
    	return 256 * 1024 * 1024;
	}
	
	public String getProperty(String key, String defaultValue) {
		return this.props.getProperty(key, defaultValue);
	}
	
    public int getHBaseBufferSize() {
    	return getInt("hbase_buffer_size", 2000);
    }
    
    public int getHBaseMaxColumnsPerPut() {
    	return getInt("hbase_max_column_per_put", 2500);   
    }
    
    public String getHBaseCompressionCodec() {
    	return this.props.getProperty("hbase_compression_codec", "GZ");
    }

    public String getKrbRealm() {
    	return this.props.getProperty("krb_realm", "");
    }
    
    public String getKrbKdc() {
    	return this.props.getProperty("krb_kdc", "");
    }

    public String getKrbJaasConf() {
    	String value = this.props.getProperty("krb_jaas", null);
    	if (value == null) {
    		return null;
    	}
    	File f = new File(value);
    	
    	if (f.getAbsoluteFile().exists())
    		return f.getAbsoluteFile().toString();
    	else
    		return null;
    }

    public int getMinkePageSize() {
        return getInt("minke.page-size", 16 * MB);
    }
    
    public int getMinkeFileSize() {
        return getInt("minke.file-size", 1 * GB);
    }
    
    public long getMinkeSize() {
        return getLong("minke.size", Long.MAX_VALUE);
    }
    
    public long getCacheSize() {
        return getLong("cache.size", 10l * GB);
    }
    
    private long getLong(String key, long defaultValue) {
        long value = defaultValue;
        String s = this.props.getProperty(key);
        if (s != null && s.trim() != "") {
            value = Long.parseLong(s);
        }
        return value;
    }
    
	private int getInt(String key, int defaultValue) {
		int value = defaultValue;
		String s = this.props.getProperty(key);
		if (s != null && s.trim() != "") {
			value = Integer.parseInt(s);
		}
		return value;
	}
	
    private boolean getBoolean(String key, boolean defaultValue) {
        boolean value = defaultValue;
        String s = this.props.getProperty(key);
        if (s != null && s.trim() != "") {
            value = Boolean.parseBoolean(s);
        }
        return value;
    }
    
	public boolean isSynchronizerEnabled() {
	    return getBoolean("minke.synchronizer", true);
	}
	
	/**
	 * when recycling files, rename them to ".junk" instead of deleting from file system
	 * 
	 * @return
	 */
	public boolean isFakeDeletetionEnabled() {
        return getBoolean("minke.fake-deletion", false);
	}
	
	public Properties getProperties() {
	    return this.props;
	}

    public int getTabletSize() {
        int result = getInt("humpback.tablet.file.size", (int)SizeConstants.mb(64));
        return result;
    }
    
    public String getStorageEngineName() {
        String result = this.props.getProperty("humpback.storage-engine");
        return (result == null) ? "minke" : result; 
    }

    /**
     * is cache verification enabled ?
     * @return 0:diabled; 1:only after fetch; 2:always
     */
    public int getCacheVerificationMode() {
        return getInt("cache.verification-mode", 0);
    }
}
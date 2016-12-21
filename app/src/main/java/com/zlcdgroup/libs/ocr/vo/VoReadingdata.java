package com.zlcdgroup.libs.ocr.vo;


import java.math.BigDecimal;



/**
 * 抄表数据
 * 
 * @author louis
 *
 */
public class VoReadingdata extends Readingdata {

	public  static  String[]  auditStatusS={"","待审","已初审","终审通过","审核不通过"};

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -6540946317779779315L;

	private String rdTimeStr;
	private String rdReadingRealStr;
	private String rdRfidstatusStr;//标签
	private String rdLoopStr;//轮回
	private String rdWarningstatusStr;//警告
	private String rdUnReadTypeStr;//无法抄读
	private String rdAuditLastTimeStart;
    private String rdAuditLastTimeStop;
    private String rdEstimateTimeStart;
    private String rdEstimateTimeStop;
    private Long rdReadingStart;
    private Long rdReadingStop;
    private String rdUsedWaterStr;
	
	private String meterId;
	private String meterNumber;
	private String meterAddr;
	private String meterClientaddr;
	private String meterClientnumber;
	private String meterClientname;
	private Integer meterStatus;
	private Integer meterDn;
	private String meterRfid;
	private String meterStatusStr;
	private BigDecimal minReading;
	private BigDecimal meterMinReading;
	private BigDecimal meterPrecision;
	private BigDecimal meterMaxScale;
	private Long meterLastreading;
	private Long meterLastreadingtime;
	private String meterListnumber;
	private String meterBd09lng;
	private String meterBd09lat;
	private String meterNatureImg;
	private String jobName;
	private String meterJobId;
	private String listNumber;
	
	private Integer zhimaExp;
	private Integer zhiduExp;
	
	private String stime;
	private String etime;
	
	private String userIds;//抄表员ID数据
	private Long count;
	private Long allCount;
	private Long validCount;
	private Long invalidCount;
	

	


	public String getMeterNumber() {
		return meterNumber;
	}

	public void setMeterNumber(String meterNumber) {
		this.meterNumber = meterNumber;
	}

	public String getMeterAddr() {
		return meterAddr;
	}

	public void setMeterAddr(String meterAddr) {
		this.meterAddr = meterAddr;
	}

	public String getMeterClientaddr() {
		return meterClientaddr;
	}

	public void setMeterClientaddr(String meterClientaddr) {
		this.meterClientaddr = meterClientaddr;
	}

	public String getMeterClientnumber() {
		return meterClientnumber;
	}

	public void setMeterClientnumber(String meterClientnumber) {
		this.meterClientnumber = meterClientnumber;
	}

	public String getMeterClientname() {
		return meterClientname;
	}

	public void setMeterClientname(String meterClientname) {
		this.meterClientname = meterClientname;
	}

	public Integer getMeterStatus() {
		return meterStatus;
	}

	public void setMeterStatus(Integer meterStatus) {
		this.meterStatus = meterStatus;
	}

	public String getMeterStatusStr() {
		return meterStatusStr;
	}

	public void setMeterStatusStr(String meterStatusStr) {
		this.meterStatusStr = meterStatusStr;
	}

	public String getRdTimeStr() {
		return rdTimeStr;
	}

	public void setRdTimeStr(String rdTimeStr) {
		this.rdTimeStr = rdTimeStr;
	}

	public String getRdReadingRealStr() {
		return rdReadingRealStr;
	}

	public void setRdReadingRealStr(String rdReadingRealStr) {
		this.rdReadingRealStr = rdReadingRealStr;
	}

	public String getRdRfidstatusStr() {
		return rdRfidstatusStr;
	}

	public void setRdRfidstatusStr(String rdRfidstatusStr) {
		this.rdRfidstatusStr = rdRfidstatusStr;
	}

	public String getRdLoopStr() {
		return rdLoopStr;
	}

	public void setRdLoopStr(String rdLoopStr) {
		this.rdLoopStr = rdLoopStr;
	}

	public String getRdWarningstatusStr() {
		return rdWarningstatusStr;
	}

	public void setRdWarningstatusStr(String rdWarningstatusStr) {
		this.rdWarningstatusStr = rdWarningstatusStr;
	}

	public String getRdUnReadTypeStr() {
		return rdUnReadTypeStr;
	}

	public void setRdUnReadTypeStr(String rdUnReadTypeStr) {
		this.rdUnReadTypeStr = rdUnReadTypeStr;
	}

	public BigDecimal getMinReading() {
		return minReading;
	}

	public void setMinReading(BigDecimal minReading) {
		this.minReading = minReading;
	}

	public BigDecimal getMeterMinReading() {
		return meterMinReading;
	}

	public void setMeterMinReading(BigDecimal meterMinReading) {
		this.meterMinReading = meterMinReading;
	}

	public BigDecimal getMeterPrecision() {
		return meterPrecision;
	}

	public void setMeterPrecision(BigDecimal meterPrecision) {
		this.meterPrecision = meterPrecision;
	}

	public BigDecimal getMeterMaxScale() {
		return meterMaxScale;
	}

	public void setMeterMaxScale(BigDecimal meterMaxScale) {
		this.meterMaxScale = meterMaxScale;
	}

	public Long getMeterLastreading() {
		return meterLastreading;
	}

	public void setMeterLastreading(Long meterLastreading) {
		this.meterLastreading = meterLastreading;
	}

	public Long getMeterLastreadingtime() {
		return meterLastreadingtime;
	}

	public void setMeterLastreadingtime(Long meterLastreadingtime) {
		this.meterLastreadingtime = meterLastreadingtime;
	}

	public String getMeterListnumber() {
		return meterListnumber;
	}

	public void setMeterListnumber(String meterListnumber) {
		this.meterListnumber = meterListnumber;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getListNumber() {
		return listNumber;
	}

	public void setListNumber(String listNumber) {
		this.listNumber = listNumber;
	}

	public Integer getZhimaExp() {
		return zhimaExp;
	}

	public void setZhimaExp(Integer zhimaExp) {
		this.zhimaExp = zhimaExp;
	}

	public Integer getZhiduExp() {
		return zhiduExp;
	}

	public void setZhiduExp(Integer zhiduExp) {
		this.zhiduExp = zhiduExp;
	}

	public String getStime() {
		return stime;
	}

	public void setStime(String stime) {
		this.stime = stime;
	}

	public String getEtime() {
		return etime;
	}

	public void setEtime(String etime) {
		this.etime = etime;
	}



    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getAllCount() {
        return allCount;
    }

    public void setAllCount(Long allCount) {
        this.allCount = allCount;
    }

    public String getUserIds() {
        return userIds;
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }

    public Long getValidCount() {
        return validCount;
    }

    public void setValidCount(Long validCount) {
        this.validCount = validCount;
    }

    public Long getInvalidCount() {
        return invalidCount;
    }

    public void setInvalidCount(Long invalidCount) {
        this.invalidCount = invalidCount;
    }

    public Long getRdReadingStart() {
        return rdReadingStart;
    }

    public void setRdReadingStart(Long rdReadingStart) {
        this.rdReadingStart = rdReadingStart;
    }

    public Long getRdReadingStop() {
        return rdReadingStop;
    }

    public void setRdReadingStop(Long rdReadingStop) {
        this.rdReadingStop = rdReadingStop;
    }

    public String getRdAuditLastTimeStart() {
        return rdAuditLastTimeStart;
    }

    public void setRdAuditLastTimeStart(String rdAuditLastTimeStart) {
        this.rdAuditLastTimeStart = rdAuditLastTimeStart;
    }

    public String getRdAuditLastTimeStop() {
        return rdAuditLastTimeStop;
    }

    public void setRdAuditLastTimeStop(String rdAuditLastTimeStop) {
        this.rdAuditLastTimeStop = rdAuditLastTimeStop;
    }

    public String getRdEstimateTimeStart() {
        return rdEstimateTimeStart;
    }

    public void setRdEstimateTimeStart(String rdEstimateTimeStart) {
        this.rdEstimateTimeStart = rdEstimateTimeStart;
    }

    public String getRdEstimateTimeStop() {
        return rdEstimateTimeStop;
    }

    public void setRdEstimateTimeStop(String rdEstimateTimeStop) {
        this.rdEstimateTimeStop = rdEstimateTimeStop;
    }

    public String getMeterJobId() {
        return meterJobId;
    }

    public void setMeterJobId(String meterJobId) {
        this.meterJobId = meterJobId;
    }

    public String getMeterId() {
        return meterId;
    }

    public void setMeterId(String meterId) {
        this.meterId = meterId;
    }

    public Integer getMeterDn() {
        return meterDn;
    }

    public void setMeterDn(Integer meterDn) {
        this.meterDn = meterDn;
    }

    public String getMeterRfid() {
        return meterRfid;
    }

    public void setMeterRfid(String meterRfid) {
        this.meterRfid = meterRfid;
    }

	public String getMeterBd09lng() {
		return meterBd09lng;
	}

	public void setMeterBd09lng(String meterBd09lng) {
		this.meterBd09lng = meterBd09lng;
	}

	public String getMeterBd09lat() {
		return meterBd09lat;
	}

	public void setMeterBd09lat(String meterBd09lat) {
		this.meterBd09lat = meterBd09lat;
	}

	public String getMeterNatureImg() {
		return meterNatureImg;
	}

	public void setMeterNatureImg(String meterNatureImg) {
		this.meterNatureImg = meterNatureImg;
	}

    public String getRdUsedWaterStr() {
        return rdUsedWaterStr;
    }

    public void setRdUsedWaterStr(String rdUsedWaterStr) {
        this.rdUsedWaterStr = rdUsedWaterStr;
    }
	
}

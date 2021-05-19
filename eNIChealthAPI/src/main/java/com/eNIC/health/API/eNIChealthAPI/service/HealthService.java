package com.eNIC.health.API.eNIChealthAPI.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.eNIC.health.API.eNIChealthAPI.entity.AddressDetail;
import com.eNIC.health.API.eNIChealthAPI.entity.BloodType;
import com.eNIC.health.API.eNIChealthAPI.entity.CommonGeneralDetail;
import com.eNIC.health.API.eNIChealthAPI.entity.CommonHealthRecord;
import com.eNIC.health.API.eNIChealthAPI.entity.ContactDetail;
import com.eNIC.health.API.eNIChealthAPI.entity.GeneralDetail;
import com.eNIC.health.API.eNIChealthAPI.entity.HealthRecord;
import com.eNIC.health.API.eNIChealthAPI.entity.OrganizationDetail;
import com.eNIC.health.API.eNIChealthAPI.entity.Useraccount;
import com.eNIC.health.API.eNIChealthAPI.repository.AddressDetailRepository;
import com.eNIC.health.API.eNIChealthAPI.repository.BloodTypeRepository;
import com.eNIC.health.API.eNIChealthAPI.repository.ContactDetailRepository;
import com.eNIC.health.API.eNIChealthAPI.repository.GeneralDetailRepository;
import com.eNIC.health.API.eNIChealthAPI.repository.HealthRecordRepository;
import com.eNIC.health.API.eNIChealthAPI.repository.OrgRepository;
import com.eNIC.health.API.eNIChealthAPI.repository.UserAccRepository;


@Service
public class HealthService {

	@Autowired
	GeneralDetailRepository generalDetailRepository;
	
	@Autowired
	AddressDetailRepository addressDetailRepository;
	
	@Autowired
	BloodTypeRepository bloodTypeRepository;
	
	@Autowired
	ContactDetailRepository contactDetailRepository;
	
	@Autowired
	UserAccRepository userAccRepository;
	
	@Autowired
	HealthRecordRepository healthRecordRepository;
	
	@Autowired
	OrgRepository orgRepository;

	@Transactional
	public CommonHealthRecord saveHealthRecord(CommonHealthRecord chr) {
		
		GeneralDetail generalDetail = generalDetailRepository.findByNic(chr.getNicNo());		
		System.out.println(chr.getNicNo());
		Useraccount useraccount = userAccRepository.findByUsername(chr.getUsername());
		System.out.println(useraccount);
		
		if (!(generalDetail ==  null || useraccount == null)) {
			HealthRecord healthRecord = new HealthRecord();
			healthRecord.setIdHealthRecord(0);
			healthRecord.setHealthRecord(chr.getHealthRecord());
			healthRecord.setHealthReport(chr.getHealthReport());
			healthRecord.setDiscription(chr.getDiscription());
			healthRecord.setRecordDate(new Date(System.currentTimeMillis()));
			
			healthRecord.setIdGeneralDetail(generalDetail);
			healthRecord.setIdUseraccount(useraccount);
			HealthRecord savedHealthRecord = healthRecordRepository.save(healthRecord);

			chr.setIdHealthRecord(savedHealthRecord.getIdHealthRecord());
			chr.setRecordDate(savedHealthRecord.getRecordDate());
			chr.setIdGeneralDetail(savedHealthRecord.getIdGeneralDetail());
			chr.setHealthRecord(savedHealthRecord.getHealthRecord());
			chr.setUsername(null);
			
		}else {
//			throw new ResourceNotFoundException("Either the General detail or User account you provided is incorrect");
		}
		
		return chr;
	}
	
	@Transactional
	public BloodType saveBloodType(String nicNo, String bloodType) {
		System.out.println(nicNo+":::::::::::::::"+bloodType);
		GeneralDetail generalDetail = generalDetailRepository.findByNic(nicNo);	
		System.out.println(generalDetail.getIdGeneralDetail());
		BloodType relType = bloodTypeRepository.getBloodType(bloodType);
		System.out.println(relType.getBloodType());
		generalDetail.setIdGeneralDetail(generalDetail.getIdGeneralDetail());
		generalDetail.setIdBloodType(relType);
		generalDetailRepository.save(generalDetail);
				
		return relType;
	}
	
	
	@Transactional
	public Map<String, Object> viewHealthRecords(String detail, String  type) {
		
		HashMap<String, Object> results = new HashMap<String, Object>();
		GeneralDetail activeGeneralDetail = null;
		List<CommonHealthRecord> ccrs = new ArrayList<CommonHealthRecord>();	
		
		List<GeneralDetail> searchedGd = null;
		int gdId = 0;
		switch (type.toLowerCase()) {
		case "nicno":
			
			System.out.println("Switched");
			searchedGd = generalDetailRepository.findPersonAll(detail);	
			activeGeneralDetail = generalDetailRepository.findByNic(detail);
			break;
			
		case "fingerprint":	
			

			searchedGd = generalDetailRepository.findPersonFinAll(detail);	
			activeGeneralDetail = generalDetailRepository.findByFingerprint(detail);
			break;
			
		default:
			break;
		}
		
		if (!searchedGd.isEmpty()) {
			
			System.out.println("Have Records");
			for (int i = 0; i < searchedGd.size(); i++) {
				GeneralDetail gd = searchedGd.get(i);
				gdId = gd.getIdGeneralDetail();
				System.out.println(gdId);
				
				List<HealthRecord> searchedCr = healthRecordRepository.getHealthRecordById(gdId);
				System.out.println("Hi");
				if (!searchedCr.isEmpty()) {
					System.out.println("Have Records");
					for (int j = 0; j < searchedCr.size(); j++) {
						CommonHealthRecord ccr = new CommonHealthRecord();
						ccr.setIdHealthRecord(searchedCr.get(j).getIdHealthRecord());
						ccr.setHealthRecord(searchedCr.get(j).getHealthRecord());
						ccr.setHealthReport(searchedCr.get(j).getHealthReport());
						ccr.setDiscription(searchedCr.get(j).getDiscription());
						ccr.setRecordDate(searchedCr.get(j).getRecordDate());
											
						System.out.println("Useracc ID:"+searchedCr.get(j).getIdUseraccount().getIdUseraccount());
						OrganizationDetail organizationDetail = orgRepository.getOrganization(searchedCr.get(j).getIdUseraccount().getIdUseraccount());
						ccr.setOrgName(organizationDetail.getOrganizationName());
						
						ccrs.add(ccr);
					}
				}else {
					
//					throw new DFileNotFoundException("There are no criminal history for this person");
				}
				
			}
			
			
		 
		}else {
//			throw new ResourceNotFoundException("There are no searched general details for the data you provided");
		}
		
		CommonGeneralDetail cgd = new CommonGeneralDetail();
	
		if (activeGeneralDetail != null) {
			ContactDetail contactDetail = contactDetailRepository.getContact(activeGeneralDetail.getIdGeneralDetail());
			List<AddressDetail> addressDetails = addressDetailRepository.getAddresses(activeGeneralDetail.getIdGeneralDetail());
			
			cgd.setIdGeneralDetail(activeGeneralDetail.getIdGeneralDetail());
			cgd.setNicNo(activeGeneralDetail.getNicNo());
			cgd.setFamilyName(activeGeneralDetail.getFamilyName());
			cgd.setName(activeGeneralDetail.getName());
			cgd.setSurName(activeGeneralDetail.getSurName());
			cgd.setDob(activeGeneralDetail.getDob());
			cgd.setGender(activeGeneralDetail.getGender());
			cgd.setCivilStatus(activeGeneralDetail.getCivilStatus());
			
			
			cgd.setIdContactDetail(contactDetail.getIdContactDetail());
			cgd.setResidentNo(contactDetail.getResidentNo());
			cgd.setMobileNo(contactDetail.getMobileNo());
			cgd.setEmailAddress(contactDetail.getEmailAddress());
			
			for (int i = 0; i < addressDetails.size(); i++) {
				AddressDetail addressDetail = addressDetails.get(i);
				if (addressDetail.getAddressType().equalsIgnoreCase("permanent")) {
					cgd.setIdAddressPermanentDetail(addressDetail.getIdAddressDetail());
					cgd.setAddressPermanentNo(addressDetail.getAddressNo());
					cgd.setAddressPermanentStreet1(addressDetail.getAddressStreet1());
					cgd.setAddressPermanentStreet2(addressDetail.getAddressStreet2());
					cgd.setAddressPermanentCity(addressDetail.getAddressCity());
				}
				if (addressDetail.getAddressType().equalsIgnoreCase("resident")) {
					cgd.setIdAddressResidentDetail(addressDetail.getIdAddressDetail());
					cgd.setAddressResidentNo(addressDetail.getAddressNo());
					cgd.setAddressResidentStreet1(addressDetail.getAddressStreet1());
					cgd.setAddressResidentStreet2(addressDetail.getAddressStreet2());
					cgd.setAddressResidentCity(addressDetail.getAddressCity());
				}
			}
					
			
		}
		
		BloodType relBtp = bloodTypeRepository.getPersonBloodType(gdId);
		if (relBtp != null) {
			cgd.setIdBloodType(relBtp.getIdBloodType());
			cgd.setBloodType(relBtp.getBloodType());
		}
	
		
		results.put("Ills", ccrs);
		results.put("Generals", cgd);
	
		return results;
	}
}

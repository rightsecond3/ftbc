package com.project;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import vo.ProjectVO;

/*
 * 
 */
@Repository
public class ProjectDao {
	private static final Logger logger = LoggerFactory.getLogger(ProjectDao.class);
	
	@Autowired
	public SqlSessionTemplate sqlSessionTemplate = null;
	

	//퍼블릭키 가져오기 - v
	public String getPublicKey(String mem_email) {
		String publicKey = sqlSessionTemplate.selectOne("getPublicKey",mem_email);
		logger.info(publicKey);
		return publicKey;
	}
	
	//모든 프로젝트  - v
	public List<ProjectVO> allProjects() {
		List<ProjectVO> allProjects = new ArrayList<>();
		allProjects = sqlSessionTemplate.selectList("allProjects");
		return allProjects;
	}
	
	//키워드별 프로젝트 - v
	public List<ProjectVO> getKeywordProjects(String keyword) {
		List<ProjectVO> keywordProjects = new ArrayList<>();
		keywordProjects = sqlSessionTemplate.selectList("keywordProjects",keyword);
		return keywordProjects;
	}
	//카테고리별 프로젝트  - v
	public List<ProjectVO> getCategoryProjects(Map<String, Object> pMap) {
		List<ProjectVO> categoryProjects = new ArrayList<>();
		categoryProjects = sqlSessionTemplate.selectList("categoryProjects",pMap);
		return categoryProjects;
	}

	//내가 펀딩한 프로젝트 - p
	public List<ProjectVO> getFundedProject(List<String> projectCodes) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("projectCodes",projectCodes);
		List<ProjectVO> fundedProject = new ArrayList<>();
		fundedProject = sqlSessionTemplate.selectList("fundedProject",map);
		return fundedProject;
	}
	//내가 만든 프로젝트 - p
	public List<ProjectVO> getMyProjects(String mem_email) {
		List<ProjectVO> myProjects = new ArrayList<>();
		myProjects = sqlSessionTemplate.selectList("myProjects",mem_email);
		return myProjects;
	}
	//프로젝트 상세보기 - v
	public ProjectVO getProjectDetail(String projectCode) {
		ProjectVO projectDetail =null;
		projectDetail = sqlSessionTemplate.selectOne("projectDetail", projectCode);
		return projectDetail;
	}
	
	//프로젝트 생성하기 - v
	public void projectcreate(Map<String, Object> pMap) {
		sqlSessionTemplate.update("projectcreate",pMap);
	}

	//스토리텔링부분 INSERT - v
	public void storytellinginsert(Map<String, Object> pMap) {
		sqlSessionTemplate.update("storytellinginsert",pMap);
	}
	
	//아웃라인부분 INSERT - v 
	public void pjoutlineinsert(Map<String, Object> pMap) {
		sqlSessionTemplate.update("pjoutlineinsert",pMap);
	}
	
	//펀딩부분 INSERT - p
	public void fundinginsert(Map<String, Object> pMap) {
		sqlSessionTemplate.update("fundinginsert",pMap);
	}
	//?
	public List<ProjectVO> recommnadProjects() {
		List<ProjectVO> recommnadProjects = new ArrayList<>();
		recommnadProjects = sqlSessionTemplate.selectList("allProjects");
		return recommnadProjects;
	}
	//?
	public List<ProjectVO> newProjects() {
		List<ProjectVO> newProjects = new ArrayList<>();
		newProjects = sqlSessionTemplate.selectList("newProjects");
		return newProjects;
	}
	//프로젝트 코드 생성하기 
	public Map<String,Object> projectCode(Map<String, Object> pMap) {
		sqlSessionTemplate.selectOne("proc_procode",pMap);
		return pMap;	
	}

	public List<Map<String, Object>> getGift(String projectCode) {
		 List<Map<String, Object>> giftList = new ArrayList<>();
		 giftList = sqlSessionTemplate.selectList("getGift",projectCode);
		return giftList;
	}

	public List<Map<String, Object>> getGiftOption(List<String> giftCode) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("giftCode",giftCode);
 		List<Map<String, Object>> giftOptionList = new ArrayList<>();
		giftOptionList = sqlSessionTemplate.selectList("getGiftOption",map);
		return giftOptionList;
	}
	//선물부분 INSERT

		//상품옵션부분 INSERT
		public int giftoptioninsert(Map<String, Object> pMap) {
			int result = 0;
			sqlSessionTemplate.update("giftoptioninsert",pMap);
			return result;
		}
		public String giftCode(Map<String, Object> pMap) {
			String gift_code = null;
			sqlSessionTemplate.selectOne("proc_giftcode",pMap);
			return gift_code;
		}

		public String giftOptionCode(Map<String, Object> pMap) {
			String gop_code = null;
			gop_code = sqlSessionTemplate.selectOne("proc_giftoption",pMap);
			return gop_code;
		}

		public List<Map<String, Object>> getProjectCodes() {
			List<Map<String, Object>> proCodes = new ArrayList<Map<String,Object>>();
			proCodes = sqlSessionTemplate.selectList("getProjectCodes");			
			return proCodes;
		}

		public List<ProjectVO> getProject(List<String> codeList) {
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("projectCodes",codeList);
			List<ProjectVO> allProjects = new ArrayList<>();
			allProjects = sqlSessionTemplate.selectList("getProjects",map);
			return allProjects;
		}

		public List<ProjectVO> getAllProject() {
			List<ProjectVO> allProjects = new ArrayList<>();
			allProjects = sqlSessionTemplate.selectList("allProjects");
			return allProjects;
		}

		// 선물부분 INSERT - 전역 변수
	   List<Map<String, Object>> insertMap = new ArrayList<>();

	   public int giftinsert(Map<String, Object> pMap) { // 선물코드 생성하고 선물코드만 따로 insert해주는 부분
	      int result = 0;
	      for (int i = 0; i < 10; i++) {
	         if (pMap.get("minDonationMoneyOutput" + i) != null) {
	            Map<String, Object> rMap = new HashMap<>();
	            sqlSessionTemplate.selectOne("proc_giftcode", pMap);
	            sqlSessionTemplate.update("giftCodeInsert", pMap);
	            rMap.put("gift_code", pMap.get("gift_code").toString());
	            insertMap.add(rMap);// 이거에 담은것을 아래 gift테이블의 다른컬럼들 insert해줄때 조건으로 사용하기 위해 넣음.

	         } else {
	            break;
	         }
	      }
	      return result;
	   }

	   public int giftupdate(Map<String, Object> pMap) {
	      int result = 0;
	      List<Map<String, Object>> lMap = new ArrayList<>();
	      for (int i = 0; i < 10; i++) {
	         if (pMap.get("minDonationMoneyOutput" + i) != null) {
	            Map<String, Object> rMap = new HashMap<>();
	            rMap.put("minDonationMoneyOutput", pMap.get("minDonationMoneyOutput" + i).toString());
	            rMap.put("giftTextAreaOutput", pMap.get("giftTextAreaOutput" + i).toString());
	            rMap.put("deliveryDayOutput", pMap.get("deliveryDayOutput" + i).toString());
	            rMap.put("limitedQuantityInput", pMap.get("limitedQuantityInput" + i).toString());
	            rMap.put("gift_isinclude", pMap.get("gift_isinclude").toString());
	            rMap.put("gift_code", insertMap.get(i).get("gift_code").toString());
	            lMap.add(rMap);
	         } else {
	            break;
	         }
	      }
	      result = sqlSessionTemplate.update("giftinsert", lMap);
	      return result;
	   }

	   ////////////////////////////////////////////////////////////////
	   // 상품옵션부분 INSERT
	   List<Map<String, Object>> gopcodeList = new ArrayList<>();

	   public void gopCodeInsert(Map<String, Object> pMap) {
	      logger.info("gopCodeInsert 호출 성공");
	      // List<Map<String,Object>> gopList = new ArrayList<>();
	      // Map<String,Object> gopMap = new HashMap<>();
	      for (int i = 0; i < pMap.size(); i++) {
	         for (int j = 0; j < pMap.size(); j++) {
	            Map<String, Object> rMap = new HashMap<>();
	            if (insertMap.size() > i) {
	               rMap.put("gift_code", insertMap.get(i).get("gift_code").toString());
	            } else {
	               break;
	            }
	            // gopMap.put("gift_code",insertMap.get(i).get("gift_code").toString()); //
	            // gopList.add(rMap);
	            if (pMap.get("GiftUlListName" + j + i) != null) {
	               sqlSessionTemplate.selectOne("proc_giftoption", rMap);
	            } else {
	               break;
	            }
	            gopcodeList.add(rMap);
	            sqlSessionTemplate.update("gopCodeInsert", rMap);
	         }
	      }
	   }

	   //////////////////////////////////////
	   // 여기서 넣어야할것이 그 gopcode에 맞는 곳에 insert 해야한다.
	   public int gopInsert(Map<String, Object> pMap) {
	      int result = 0;
	      List<Map<String, Object>> giftList = new ArrayList<>();
	      for (int i = 0; i < 10; i++) {// 선물의 개수
	         for (int j = 0; j < 5; j++) {// 옵션의 개수
	            if (pMap.get("GiftUlListName" + j + i) != null) {
	               Map<String, Object> rMap = new HashMap<>();
	               rMap.put("GiftUlListName", pMap.get("GiftUlListName" + j + i));
	               rMap.put("GiftUlListQuantity", pMap.get("GiftUlListQuantity" + j + i));
	               rMap.put("itemListOptionOutput", pMap.get("itemListOptionOutput" + j));
	               if (i == 0) {
	                  rMap.put("gop_code", gopcodeList.get(j).get("gop_code").toString());
	               } else if (i == 1) {
	                  rMap.put("gop_code", gopcodeList.get(3 + j).get("gop_code").toString());
	               } else if (i == 2) {
	                  rMap.put("gop_code", gopcodeList.get(6 + j).get("gop_code").toString());
	               }
	               giftList.add(rMap);
	            } else {
	               break;
	            }
	         }
	      }

	      result = sqlSessionTemplate.update("gopInsert", giftList);
	      return result;
	   }
}

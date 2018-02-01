package com.fivebit.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author ranran
 * @version V1.0
 * @Title:
 * @Package com.fivebit.notice.dao
 * @Description:
 * @date 2017/6/14 18:09
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/applicationContext.xml")
public class TrackRecodeDaoTest {

    @Autowired

    @Test
    public void testTrackRecodeDao() throws Exception {
        //boolean result = trackRecodeDao.updateTrackRecodeStatus("track_url_suffix_test", 5);
        //System.out.println(result);
    }

}

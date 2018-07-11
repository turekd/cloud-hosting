package it.dturek.cloudhosting.service.impl;

import it.dturek.cloudhosting.dao.ResourceDao;
import it.dturek.cloudhosting.domain.jpa.Resource;
import it.dturek.cloudhosting.domain.jpa.User;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

@RunWith(EasyMockRunner.class)
public class ResourceServiceImplTest {

    @TestSubject
    private ResourceServiceImpl resourceService = new ResourceServiceImpl();

    @Mock
    private ResourceDao resourceDao;

    @Mock
    private User user;

    @Test
    public void testModifyNameIfDuplicatedWhenNoDuplicated() throws Exception {
        expect(resourceDao.findByName("test", null, user, Resource.Type.DIRECTORY)).andReturn(null);
        replay(resourceDao);
        assertEquals("test", resourceService.modifyNameIfDuplicated("test", user, null, Resource.Type.DIRECTORY));
        verify(resourceDao);
    }

    @Test
    public void testModifyNameIfDuplicatedWhen1Duplicate() throws Exception {
        expect(resourceDao.findByName("test", null, user, Resource.Type.DIRECTORY)).andReturn(createResource("test"));
        expect(resourceDao.findByName("test (1)", null, user, Resource.Type.DIRECTORY)).andReturn(null);
        replay(resourceDao);
        assertEquals("test (1)", resourceService.modifyNameIfDuplicated("test", user, null, Resource.Type.DIRECTORY));
        verify(resourceDao);
    }

    @Test
    public void testModifyNameIfDuplicatedWhen2Duplicates() throws Exception {
        expect(resourceDao.findByName("test", null, user, Resource.Type.DIRECTORY)).andReturn(createResource("test"));
        expect(resourceDao.findByName("test (1)", null, user, Resource.Type.DIRECTORY)).andReturn(createResource("test (1)"));
        expect(resourceDao.findByName("test (2)", null, user, Resource.Type.DIRECTORY)).andReturn(null);
        replay(resourceDao);
        assertEquals("test (2)", resourceService.modifyNameIfDuplicated("test", user, null, Resource.Type.DIRECTORY));
        verify(resourceDao);
    }

    private Resource createResource(String name) {
        Resource resource = new Resource();
        resource.setName(name);
        return resource;
    }

}
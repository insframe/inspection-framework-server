package com.insframe.server.data.repository;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import com.insframe.server.config.WebAppConfigurationAware;
import com.insframe.server.error.AssignmentAccessException;
import com.insframe.server.error.AssignmentStorageException;
import com.insframe.server.error.InspectionObjectAccessException;
import com.insframe.server.error.InspectionObjectStorageException;
import com.insframe.server.error.UserAccessException;
import com.insframe.server.error.UserStorageException;
import com.insframe.server.model.Assignment;
import com.insframe.server.model.FileMetaData;
import com.insframe.server.model.InspectionObject;
import com.insframe.server.model.Task;
import com.insframe.server.model.User;
import com.insframe.server.service.AssignmentService;
import com.insframe.server.service.GridFsService;
import com.insframe.server.service.InspectionObjectService;
import com.insframe.server.service.UserService;

public class AssignmentServiceTest extends WebAppConfigurationAware {
	
	@Autowired
	private AssignmentService assignmentService;
	@Autowired
	private UserService userService;
	@Autowired
	private InspectionObjectService inspectionObjectService;
	@Autowired
	private GridFsService gridFsService;
	@Autowired
	private AssignmentRepository assignmentRepository;
	
	@Before
	public void init() throws FileNotFoundException, AssignmentStorageException, InspectionObjectAccessException, UserAccessException, UserStorageException, InspectionObjectStorageException, AssignmentAccessException{
		assignmentRepository.deleteAll();
		userService.deleteAll();
		inspectionObjectService.deleteAll();
		assignmentService.save(new Assignment("Check University of Mannheim", "Go to the university of Mannheim and perform the needed checks.", true));
		
		userService.save(new User("assignmentTest", "assignmentTest@gmail.com", "assignmentTest", "ROLE_ADMIN", "assignmentTest", "assignmentTest", "+49162123456", "+49162123456"));
		try {
			inspectionObjectService.save(new InspectionObject("Ernst-Walz bridge", "Ernst-Walz bridge in Heidelberg", "Heidelberg", "City of Heidelberg"));
		} catch (DuplicateKeyException e) {
			// TODO: handle exception
		}
		List<Task> tasks = new ArrayList<Task>();
		tasks.add(new Task("Check north pillar", "Check north pillar", Task.STATE_OKAY));
		tasks.add(new Task("Check south pillar", "Check south pillar", Task.STATE_OKAY));
		Assignment bridgeAssignment = new Assignment("Check Ernst-Walz", "Go to the Ernst-Walz bridge in Heidelberg and perform the needed checks.", false, 0, 
				tasks, new Date(), new Date(), userService.findByUserName("assignmentTest"), inspectionObjectService.findByObjectName("Ernst-Walz bridge", false));
		
		InputStream is = new FileInputStream("bridge.jpg");
		
		bridgeAssignment.addAttachment(gridFsService.store(is, "bridge.jpg", "image/jpeg", new FileMetaData("Ernst-Walz bridge Heidelberg")));
		assignmentService.save(bridgeAssignment);
	}
	
	@Test
	public void executePrintAllAssignments() throws AssignmentAccessException{
		System.out.println("****** Should Show all the Assignments in the database ******");
		System.out.println("Show the 2 Assignments and their data");
		List<Assignment> AssignmentList = assignmentRepository.findAll();
		for(Assignment Assignment : AssignmentList){
			System.out.println(Assignment);
		}
		Assert.assertTrue(AssignmentList.size() == 2);
		System.out.println("****** End of Should Show All the Assignments in the database ******");
	}
	
	@Test(expected=AssignmentStorageException.class)
	public void shouldThrowExceptionDueToInvalidDates() throws AssignmentAccessException, ParseException, AssignmentStorageException, UserAccessException{
		System.out.println("****** Should throw an Exception because the dates are invalid (end date is older than start date) ******");
		List<Assignment> assignmentList = assignmentRepository.findAll();
		Assignment auxAs = new Assignment();
		for(Assignment a : assignmentList){
			if(!a.getIsTemplate()) auxAs = a;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		auxAs.setStartDate(sdf.parse("10/03/2015"));
		auxAs.setEndDate(sdf.parse("09/03/2015"));
		assignmentService.save(auxAs);
		System.out.println("****** End of Should throw an Exception because the dates are invalid (end date is older than start date) ******");
	}
	
	@Test
	public void shouldValidEqualDates() throws AssignmentAccessException, ParseException, AssignmentStorageException, UserAccessException{
		System.out.println("****** Should Valid two equal dates, start date is equal than end date ******");
		List<Assignment> assignmentList = assignmentRepository.findAll();
		Assignment auxAs = new Assignment();
		for(Assignment a : assignmentList){
			if(!a.getIsTemplate()) auxAs = a;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		auxAs.setStartDate(sdf.parse("10/03/2015"));
		auxAs.setEndDate(sdf.parse("10/03/2015"));
		Assignment a2 = assignmentService.save(auxAs);
		System.out.println("Start date: "+sdf.format(a2.getStartDate()));
		System.out.println("End date: "+sdf.format(a2.getEndDate()));
		System.out.println("****** End of Should Should Valid two equal dates, start date is equal than end date ******");
	}
	
	@Test
	public void shouldValidDifferentButValidDates() throws AssignmentAccessException, ParseException, AssignmentStorageException, UserAccessException{
		System.out.println("****** Should Valid Two Different but valid dates, Start date is older than End date ******");
		List<Assignment> assignmentList = assignmentRepository.findAll();
		Assignment auxAs = new Assignment();
		for(Assignment a : assignmentList){
			if(!a.getIsTemplate()) auxAs = a;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		auxAs.setStartDate(sdf.parse("10/03/2015"));
		auxAs.setEndDate(sdf.parse("15/03/2015"));
		Assignment a2 = assignmentService.save(auxAs);
		System.out.println("Start date: "+sdf.format(a2.getStartDate()));
		System.out.println("End date: "+sdf.format(a2.getEndDate()));
		System.out.println("****** End of Should Valid Two Different but valid dates, Start date is older than End date ******");
	}
//	@Test
//	public void executeAssignmentByIdDeletion() {
//		System.out.println("****** Should Delete Assignment by ID ******");
//		System.out.println("Now the first returned inspection object will be deleted.");
//		List<Assignment> AssignmentList = assignmentService.findAll();
//		Assert.assertNotNull(assignmentService.findById(AssignmentList.get(0).getId()));
//		assignmentService.deleteAssignmentByID(AssignmentList.get(0).getId());
//		Assert.assertNull(assignmentService.findById(AssignmentList.get(0).getId()));
//		AssignmentList = assignmentService.findAll();
//		Assert.assertTrue(AssignmentList.size() == 3);
//		System.out.println("First inspection object was deleted.");
//		System.out.println("****** End of deletion of Assignment by ID ******");
//	}
	
//	@Test
//	public void executeShowAssignmentByObjectName(){
//		System.out.println("****** Should show Assignment found by ObjectName ******");
//		Assignment Assignment = (Assignment) assignmentService.findByObjectName("University of Mannheim");
//		
//		Assert.assertNotNull(Assignment);
//		Assert.assertEquals("University of Mannheim", Assignment.getObjectName());
//		Assert.assertEquals("Mannheim", Assignment.getLocation());
//		Assert.assertEquals("The university of Mannheim", Assignment.getDescription());
//		Assert.assertEquals("Studentenwerk Mannheim", Assignment.getCustomerName());
//		
//		System.out.println(Assignment);
//		System.out.println("****** End of should show Assignment found by ObjectName ******");
//	}
//	
//	@Test
//	public void executeUpdateAssignment() throws AssignmentAccessException{
//		System.out.println("****** execute update Assignment ******");
//		Assignment Assignment = assignmentService.findByObjectName("University of Mannheim");
//		System.out.println(Assignment);
//		Assignment.setDescription("Mannheim University");
//		
//		assignmentService.updateById(Assignment.getId(), Assignment);
//		
//		Assignment newAssignment = assignmentService.findByObjectName("University of Mannheim");
//		System.out.println(newAssignment);
//		Assert.assertEquals("Mannheim University", newAssignment.getDescription());
//		System.out.println("****** End of execute update Assignment ******");
//	}

}

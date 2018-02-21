/**
 * 
 */
package edu.asu.plp.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import edu.asu.plp.model.AssemblyInfo;
import edu.asu.plp.model.WebASMFile;
import edu.asu.plp.service.PLPUserDB;
import edu.asu.plp.tool.backend.EventRegistry;
import edu.asu.plp.tool.backend.isa.*;
import edu.asu.plp.tool.backend.isa.events.DeviceOutputEvent;
import edu.asu.plp.tool.backend.isa.events.RegWatchRequestEvent;
import edu.asu.plp.tool.backend.isa.events.RegWatchResponseEvent;
import edu.asu.plp.tool.backend.isa.events.SimulatorControlEvent;
import edu.asu.plp.tool.backend.isa.exceptions.AssemblerException;
import edu.asu.plp.tool.backend.plpisa.assembler2.*;

import edu.asu.plp.user.dao.impl.JdbcUserDAO;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.json.JSONObject;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;

import edu.asu.SimulatorFiles.*;
import edu.asu.plp.tool.backend.plpisa.sim.*;
import edu.asu.plp.tool.core.ISAModule;
import edu.asu.plp.tool.core.ISARegistry;
import edu.asu.plp.tool.prototype.ApplicationSettings;
import edu.asu.plp.tool.prototype.EmulationWindow;
import edu.asu.plp.tool.prototype.ProjectAssemblyDetails;
import edu.asu.plp.tool.prototype.Main.ApplicationEventBusEventHandler;
import edu.asu.plp.tool.prototype.model.Project;
import edu.asu.plp.tool.prototype.model.Theme;
import edu.asu.plp.tool.prototype.model.ThemeRequestCallback;
import edu.asu.plp.tool.prototype.util.Dialogues;
import edu.asu.plp.tool.prototype.view.ConsolePane;
import edu.asu.plp.tool.prototype.view.WatcherWindow.RegisterRow;
import javafx.beans.property.LongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import moore.util.Subroutine;


/**
 * @author ngoel2, Sumeet Jain, Abhilash Malla , Mukulsingh Jadhav
 *
 */

@RestController
public class PLPWebController {
	private final String PROJECT_TYPE = "plp";
	String fileStoragePath = "files/";
	HttpSession session;
	ASMImage image = null;
	private Thread simRunThread;
	private Stage stage;
	private ConsolePane console;
	private Simulator activeSimulator;
    static Logger log = Logger.getLogger(PLPWebController.class);

    /**
     * This function register the new user or get the instance of existing user
     * @param un This is the username of the new or existing user
     * @param request This is the HTTPServletRequest object
     * @return the response in JSON format as a success or failure along with the session key
     */
	@RequestMapping("/register")
	@CrossOrigin
	public String register(@RequestParam(value="un", defaultValue="guestUser") String un, HttpServletRequest request) {
		String response = "";
		String sessionKey;
		Map<String, String> responseMap = new HashMap<String, String> ();
		session = request.getSession();
		sessionKey = session.getId();
		PLPUserDB.getInstance().registerNewUser(un, session, sessionKey	);
		responseMap.put("status", "success");
		responseMap.put("session_key", sessionKey);

		try {
			response = new ObjectMapper().writeValueAsString(responseMap);
		} catch (JsonProcessingException e) {
			System.out.println("JSON parsing Error.");
			e.printStackTrace();
		}
		//response += "\"status\":\"successs\",\"session_key\":\"" + sessionKey + "\"";
		System.out.println(response);
		return response;
	}

    /**
     * This function saves the file in the server
     * @param request This is the HTTPServletRequest object
     * @return the status as success or failed
     */
	@RequestMapping(value = "/uploadFile" , method = RequestMethod.POST)
	@CrossOrigin
	public String upload(HttpServletRequest request) {

		String response = "";
		System.out.println("in upload server side");

		//org.springframework.web.multipart.MultipartHttpServletRequest
		MultipartHttpServletRequest mRequest;
		mRequest = (MultipartHttpServletRequest) request;

		java.util.Iterator<String> itr = mRequest.getFileNames();
		while (itr.hasNext()) {
			//org.springframework.web.multipart.MultipartFile
			MultipartFile mFile = mRequest.getFile(itr.next());
			String fileName = mFile.getOriginalFilename();
			System.out.println("**Saved at** "+fileStoragePath+ fileName);

			//To copy the file to a specific location in machine.
			File file = new File(fileStoragePath+fileName);
			try {
				FileCopyUtils.copy(mFile.getBytes(), file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(" server exception in file upload :(");
				e.printStackTrace();
				response += "\"status\":\"failed\"";
				return "{"+response+"}";
			} //This will copy the file to the specific location.
		}
		response += "\"status\":\"success\"";

		return "{"+response+"}";
	}

    /***
     * This function assembles the code
     * @param assembly It has all the information of the assembly
     * @param request This is the HTTPServletRequest object
     * @param session This is the HTTPSession object
     * @return the status as Ok or failed
     * @throws AssemblerException
     * @throws JsonProcessingException
     */
	@RequestMapping(value = "/assembleText" , method = RequestMethod.POST)
	@CrossOrigin
	public String assembleText(@RequestBody AssemblyInfo assembly, HttpServletRequest request, HttpSession session) throws AssemblerException, JsonProcessingException {

		System.out.println("in assemble");
		String response = "";
		Map<String, String> responseMap = new HashMap<String, String> ();
        Object o = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MDC.put("username", JdbcUserDAO.userEmailMap.get(o));
		try {
			ApplicationSettings.initialize();
			ApplicationSettings.loadFromFile("settings/plp-tool.settings");
			EventRegistry.getGlobalRegistry().register(new ApplicationEventBusEventHandler());

			String sessKey = assembly.getSessionKey();
			System.out.println("Sess: " + assembly.getSessionKey());
			session = PLPUserDB.getInstance().getUser(sessKey).getUserSession();

			String[] code = assembly.getCode();
			List<ASMFile> listASM = new ArrayList<ASMFile>();
			
			for(String c : code){
				WebASMFile asmFile = new WebASMFile(c, "main.asm");
				System.out.println("code: " + code);
				asmFile.setContent(c);
				listASM.add(asmFile);
			}
			Assembler assembler = new PLPAssembler();
			image = assembler.assemble(listASM);
			System.out.println("ID: " + session.getId());
			session.setAttribute("ASMImage", image);
			responseMap.put("status", "ok");
			log.info("Code assembled successfully!");
		}
		catch (Exception exception)
		{
		    log.error("Error in assembling! : "+exception.getMessage());
			responseMap.put("status", "failed");
			responseMap.put("message", exception.getLocalizedMessage());
			session.setAttribute("ASMImage", null);
		}
		try {
			response = new ObjectMapper().writeValueAsString(responseMap);
		} catch (JsonProcessingException e) {
			System.out.println("JSON parsing Error.");
            log.error("Error in assembling - JSON parsing! : " +e.getMessage());
			e.printStackTrace();
		}
		System.out.println(response);
		return response;
	}

	@RequestMapping(value = "/Simulator" , method = RequestMethod.POST)
	@CrossOrigin
	public String Simulator(@RequestBody String sessKey, HttpServletRequest request, HttpSession session) throws IOException {
		System.out.println("in Simulate eclipse");
		Map<String, String> responseMap = new HashMap<String, String> ();
		String response = "";

		System.out.println("Session id test: " + sessKey);
		session = PLPUserDB.getInstance().getUser(sessKey).getUserSession();
		System.out.println("ID in simulate: " + session.getId());

		//System.out.println("ASM OBJ is Sim :"  +  session.getAttribute("ASMImage"));
		image = (ASMImage) session.getAttribute("ASMImage");
		System.out.println("ASM OBJ in Sim :" + image);

		if(image == null){
			System.out.println("NO ASM IMAGE FOUND!");
			responseMap.put("status", "failed");
			responseMap.put("simError", "no-asm");
			session.setAttribute("simulationSuccess", false);
			session.setAttribute("simulationError", "no-asm");

		}
		else{
			Optional<ISAModule> module = ISARegistry.get().lookupByProjectType(PROJECT_TYPE);

			if (module.isPresent())
			{
				responseMap.put("status", "ok");
				ISAModule isa = module.get();
				activeSimulator = isa.getSimulator();
				activeSimulator.startListening();

				EventRegistry.getGlobalRegistry().post(
						new SimulatorControlEvent("load","", image));
				session.setAttribute("simulationSuccess", true);

			}
			else
			{	responseMap.put("status", "failed");
			responseMap.put("simError", "no-sim");
			String message = "No simulator is available for the project type: ";
			System.out.println(message);
			//Dialogues.showAlertDialogue(new IllegalStateException(message));
			session.setAttribute("simulationSuccess", false);
			session.setAttribute("simulationError", "no-sim");
			}
		}

		try {
			response = new ObjectMapper().writeValueAsString(responseMap);
		} catch (JsonProcessingException e) {
			System.out.println("JSON parsing Error.");
			e.printStackTrace();
		}
		return response;
	}



	// Run Simulation -- abhilash
	@RequestMapping(value = "/Run" , method = RequestMethod.GET)
	@CrossOrigin
	public String Run(HttpServletRequest request, HttpSession session) throws IOException {
		System.out.println("in Run method");
		Map<String, String> responseMap = new HashMap<String, String> ();
		String response = "";

		if((boolean) session.getAttribute("simulationSuccess")){
			EventRegistry.getGlobalRegistry().post(
					new SimulatorControlEvent("load", "", image));

			session.setAttribute("isSimulationRunning", true);
			simRunThread = new Thread(new Runnable(){
				public void run()
				{
					while ((boolean) (session.getAttribute("isSimulationRunning"))) {
						EventRegistry.getGlobalRegistry().post(new SimulatorControlEvent("step", "",null));
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
						}
					}
				}
			});
			simRunThread.start();
			responseMap.put("status", "ok");
		}
		else{
			responseMap.put("status", "failed");
		}
		
		try {
			response = new ObjectMapper().writeValueAsString(responseMap);
		} catch (JsonProcessingException e) {
			System.out.println("JSON parsing Error.");
			e.printStackTrace();
		}

		return response;
	}


	@RequestMapping(value = "/Stop" , method = RequestMethod.GET)
	@CrossOrigin
	public String Stop(HttpServletRequest request, HttpSession session) throws IOException {

		System.out.println("in Stop method");
		String response = "";

		EventRegistry.getGlobalRegistry().post(new SimulatorControlEvent("pause", "",null));
		EventRegistry.getGlobalRegistry().post(new SimulatorControlEvent("reset", "",null));
		session.setAttribute("isSimulationRunning", false);
		if(simRunThread != null)
		{
			simRunThread.interrupt();
			simRunThread = null;
		}

		activeSimulator.stopListening();


		response = "{\"status\":\"ok\"}";
		return response;
	}

	@RequestMapping(value = "/StepSim" , method = RequestMethod.GET)
	@CrossOrigin
	public String StepSim(HttpServletRequest request, HttpSession session) throws IOException {

		String response = "";

		try
		{
			EventRegistry.getGlobalRegistry().post(
								new SimulatorControlEvent("step", "",null));
			
		}
		catch (Exception exception)
		{
			throw new IllegalStateException("No simulator is active!", exception);
			
		}
		
		response = "{\"status\":\"ok\"}";
		return response;
	}
	
	
	@RequestMapping(value = "/WatchReg" , method = RequestMethod.GET)
	@CrossOrigin
	public String WatchReg(HttpServletRequest request, HttpSession session) throws IOException {

		System.out.println("in WatchReg method");
		String response = "";
	//	websock.start();

		String registerName = "$t0";
		EventRegistry.getGlobalRegistry().post(new RegWatchRequestEvent(registerName));
		
		System.out.println("VALUE:   " + session.getAttribute("idd"));
		response = "{\"status\":\"ok\"}";
		return response;
	}


	// new class copied form main.java

	public class ApplicationEventBusEventHandler
	{
		private ApplicationEventBusEventHandler()
		{

		}

		@Subscribe
		public void HandlerDeviceOutput(DeviceOutputEvent event) {
			//do nothing
		}

		@Subscribe
		public void applicationThemeRequestCallback(ThemeRequestCallback event)
		{
			if (event.requestedTheme().isPresent())
			{
				Theme applicationTheme = event.requestedTheme().get();
				try
				{
					stage.getScene().getStylesheets().clear();
					stage.getScene().getStylesheets().add(applicationTheme.getPath());
					return;
				}
				catch (MalformedURLException e)
				{
					console.warning("Unable to load application theme "
							+ applicationTheme.getName());
					return;
				}
			}

			console.warning("Unable to load application theme.");
		}
		
		@SuppressWarnings("restriction")
		@Subscribe
		public void registerWatchResult(RegWatchResponseEvent e) {
			if (!e.isSuccess())
				throw new IllegalArgumentException("There isn't a register with the name "
						+ e.getRegisterName());
			
			String id = e.getRegisterID();
			String registerName = e.getRegisterName();
			LongProperty register = e.getRegObject();
			System.out.println("suc" + id + " -- " +  registerName + " -- " + register);
			register.addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					//watchedRegisters.refresh();
					System.out.println("Success" + id + " -- " +  registerName + " -- " + register);
					session.setAttribute("idd", register.getValue());
					//System.out.println("id is:   " + session.getAttribute("idd"));
				}
				
			});

		}

		@Subscribe
		public void deadEvent(DeadEvent event)
		{
			System.out.println("Dead Event");
			System.out.println(event.getEvent());
		}
	}
}
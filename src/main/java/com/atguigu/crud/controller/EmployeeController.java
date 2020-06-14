package com.atguigu.crud.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.atguigu.crud.bean.Employee;
import com.atguigu.crud.bean.Msg;
import com.atguigu.crud.service.EmployeeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * ����Ա��CRUD����
 * @author Cherish599
 *
 */
@Controller
public class EmployeeController {

	@Autowired
	EmployeeService employeeService;
	
	/**
	 * ����ɾ������ɾ������һ
	 * ����ɾ����1-2-3
	 * ����ɾ����1
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/emp/{ids}",method = RequestMethod.DELETE)
	public Msg deleteEmp(@PathVariable("ids") String ids) {
		//����ɾ��
		if(ids.contains("-")) {
			List<Integer> del_ids = new ArrayList<>();
			String[] str_ids = ids.split("-");
			 //for (String id : str_ids) { }
			//��װid����
			for (String string : str_ids) {
				del_ids.add(Integer.parseInt(string));
			}
			employeeService.deleteBatch(del_ids);
			
		}else {
			//����ɾ��
			int id= Integer.parseInt(ids);
			employeeService.deleteEmp(id);
		}
		return Msg.success();
	}
	
	
	
	
	
	/**
	 * ���ֱ�ӷ���ajax=PUT��ʽ������
	 * ��װ������
	 * Employee
	 * [empId=2014, empName=null, gender=null, email=null, dId=null, department=null]
	 * ���⣺
	 * �������������ݣ�
	 * ����Employee�����װ���ϡ�
	 * 
	 * ԭ��
	 * Tomcat:
	 * 		1�����������е����ݣ���װ��һ��map.
	 * 		2��request.getParameter("empName")�ͻ�����map��ȡֵ��
	 * 		3��SprngMVC��װ�������ݵ�ʱ��
	 * 				���POJO��ÿ�����Ե�ֵ����request.getParameter("email);
	 * AJAX����PUT����������Ѫ����
	 * 		PUT�����������е����ݣ�request.getParameter("empName")�ò���	
	 * 		ԭ����Tomcatһ����PUT����Ͳ����װ�������е�����Ϊmap��ֻ��POST��ʽ����Ż��װ�������е�����Ϊmap
	 * 
	 * ���������
	 * ����Ҫ��֧��ֱ�ӷ���PUT֮������Ҫ��װ���������е�����
	 * ������HttpPutFormContentFilter
	 * �������ã�
	 * 		���������е����ݽ�����װ��һ��map��
	 * 		request�����°�װ������request.getParameter()������д���ͻ���Լ���װ��map��ȡ������
	 * 
	 * Ա�����·���
	 * @param employee
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/emp/{empId}",method = RequestMethod.PUT)
	public Msg saveEmp(Employee employee,HttpServletRequest request) {
		System.out.println("�������е�ֵ��"+request.getParameter("gender"));
		System.out.println("��Ҫ���µ�Ա������"+employee);
		employeeService.updateEmp(employee);
		return Msg.success();
	}
	
	/**
	 * ����id��ѯԱ��
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/emp/{id}",method = RequestMethod.GET)
	@ResponseBody
	public Msg getEmp(@PathVariable("id") Integer id) {
		
		Employee employee = employeeService.getEmp(id);
		return Msg.success().add("emp", employee);
	}
	
	/**
	 * ����û����Ƿ����
	 * @param empName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/checkuser")
	public Msg checkuser(@RequestParam("empName") String empName) {
		//���ж��û����Ƿ��ǺϷ��ı��ʽ
		String regx = "(^[a-zA-Z0-9_-]{6,16}$)|(^[\\u2E80-\\u9FFF]{2,5})";
		if(!empName.matches(regx)) {
			return Msg.fail().add("va_msg", "�û���������6-16λӢ�������ֵ���ϻ���2-5λ����");
		}
		//���ݿ��û����ظ�У��
		boolean b = employeeService.checkUser(empName);
		if(b) {
			return Msg.success();
		}else {
			return Msg.fail().add("va_msg", "�û����ѱ�ռ��");
		}
	}
	
	
	/**
	 * Ա������
	 * 1��֧��JSR303У��
	 * 2������Hibernate-Validator
	 * @return
	 */
	@RequestMapping(value = "/emp",method = RequestMethod.POST)
	@ResponseBody
	public Msg saveEmp(@Valid Employee employee,BindingResult result) {
		if(result.hasErrors()) {
			//У��ʧ�ܣ�Ӧ�÷���ʧ�ܣ���ģ̬������ʾУ��ʧ�ܵĴ�����Ϣ��
			Map<String, Object> map = new HashMap<String, Object>();
			List<FieldError> errors = result.getFieldErrors();
			for (FieldError fieldError : errors) {
				System.out.println("������ֶ���"+fieldError.getField());
				System.out.println("������Ϣ��"+fieldError.getDefaultMessage());
				map.put(fieldError.getField(), fieldError.getDefaultMessage());
			}
			return Msg.fail().add("errorFields", map);
		}else {
			employeeService.saveEmp(employee);
			return Msg.success();
		}
	}
	
	/**
	 * ����jackson����
	 * @param pn
	 * @return
	 */
	
	@RequestMapping("/emps")
	@ResponseBody
	public Msg getEmpsWithJson(@RequestParam(value="pn",defaultValue = "1") Integer pn) {
		//�ⲻ��һ����ҳ��ѯ  
		//����PageHelper��ҳ���
		//�ڲ�ѯ֮ǰֻ��Ҫ����PageHelper.startPage(����ҳ�룬ÿҳ�����ݵĶ���)
		PageHelper.startPage(pn, 5);
		//startPage��������ŵ������ѯ����һ����ҳ��ѯ
		List<Employee> emps = employeeService.getAll();
		
		//ʹ��PageInfo��װ��ѯ��Ľ����ֻ��Ҫ��PageInfo����ҳ������ˡ�
		//��װ����ϸ�ķ�ҳ��Ϣ�����������ǲ�ѯ���������ݡ�����������ʾ��ҳ��(5)
		PageInfo page = new PageInfo(emps,5);
		return Msg.success().add("pageInfo", page);
	}
	
	
	/**
	 * ��ѯԱ�����ݣ���ҳ��ѯ��
	 */
	//@RequestMapping("/emps")
	public String getEmps(@RequestParam(value="pn",defaultValue = "1") Integer pn, 
			Model model) {
		
		//�ⲻ��һ����ҳ��ѯ  
		//����PageHelper��ҳ���
		//�ڲ�ѯ֮ǰֻ��Ҫ����PageHelper.startPage(����ҳ�룬ÿҳ�����ݵĶ���)
		PageHelper.startPage(pn, 5);
		//startPage��������ŵ������ѯ����һ����ҳ��ѯ
		List<Employee> emps = employeeService.getAll();
		
		//ʹ��PageInfo��װ��ѯ��Ľ����ֻ��Ҫ��PageInfo����ҳ������ˡ�
		//��װ����ϸ�ķ�ҳ��Ϣ�����������ǲ�ѯ���������ݡ�����������ʾ��ҳ��(5)
		PageInfo page = new PageInfo(emps,5);
		model.addAttribute("pageInfo", page);
		
		return "list";
	}
}

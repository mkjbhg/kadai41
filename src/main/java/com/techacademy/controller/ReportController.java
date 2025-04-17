package com.techacademy.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.constants.ErrorMessage;

import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.service.ReportService;
import com.techacademy.service.UserDetail;

@Controller
@RequestMapping("report")
public class ReportController {

	private final ReportService reportService;

	@Autowired
	public ReportController(ReportService reportService) {
		this.reportService = reportService;
	}

	// 日報一覧画面
	@GetMapping
	public String list(Model model) {

		model.addAttribute("listSize", reportService.findAll().size());
		model.addAttribute("reportList", reportService.findAll());

		return "report/list";
	}

	// 日報詳細画面
	@GetMapping(value = "/{id}/")
	public String detail(@PathVariable("id") Integer id, Model model) {

		model.addAttribute("report", reportService.findById(id));
		return "report/detail";
	}

// 日報新規登録画面
	@GetMapping(value = "/add")
	public String create(@ModelAttribute Report report, @AuthenticationPrincipal UserDetail userDetail, Model model) {
		model.addAttribute("employee", userDetail.getEmployee());

		return "report/new";
	}

// 日報新規登録処理
	@PostMapping(value = "/add")
	public String add(@Validated Report report, BindingResult res, Model model,
			@AuthenticationPrincipal UserDetail userDetail) {

		// 入力チェック
		if (res.hasErrors()) {
			model.addAttribute("employee", userDetail.getEmployee());
			return "report/new";
		}

		try {
			ErrorKinds result = reportService.save(report, userDetail.getEmployee());

			if (ErrorMessage.contains(result)) {
				model.addAttribute("datecheckError", ErrorMessage.getErrorValue(result));
				model.addAttribute("employee", userDetail.getEmployee());
				return "report/new";
			}

		} catch (DataIntegrityViolationException e) {
			model.addAttribute(ErrorMessage.getErrorName(ErrorKinds.DUPLICATE_EXCEPTION_ERROR),
					ErrorMessage.getErrorValue(ErrorKinds.DUPLICATE_EXCEPTION_ERROR));
			model.addAttribute("employee", userDetail.getEmployee());
			return "report/new";
		}

		return "redirect:/report";
	}

	// 従業員更新画面表示★
	@GetMapping(value = "/{id}/update")
	public String edit(@PathVariable("id") Integer id, Model model) {
		Report report = reportService.findById(id);
		model.addAttribute("report", report);
		return "report/edit"; // 更新用のHTMLファイル
	}

	@PostMapping(value = "/{id}/update")
	public String update(@PathVariable("id") Integer id, @Validated @ModelAttribute Report report, BindingResult res,
			Model model, @AuthenticationPrincipal com.techacademy.service.UserDetail userDetail) {

		// 入力チェック
		if (res.hasErrors()) {
			return "report/edit";
		}

		// 日報更新処理
		ErrorKinds result = reportService.update(report, userDetail.getEmployee(), id);

		// 「既に登録されている日付」のエラー対応
		if (result == ErrorKinds.DATECHECK_ERROR) {
			model.addAttribute("dateError", "既に登録されている日付です");
			return "report/edit";
		}

		// 成功時
		return "redirect:/report/list";
	}
}
package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.entity.Report;
import com.techacademy.repository.ReportRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

	private final ReportRepository reportRepository;

	public ReportService(ReportRepository reportRepository) {
		this.reportRepository = reportRepository;

	}

	// 日報一覧表示処理
	public List<Report> findAll() {
		return reportRepository.findAll();
	}

	public Report findById(Integer id) {
		// findByIdで検索
		Optional<Report> option = reportRepository.findById(id);
		// 取得できなかった場合はnullを返す
		Report report = option.orElse(null);
		return report;

	}
		// ★ ここに追加：従業員から日報一覧取得
		public List<Report> findByEmployee(Employee employee) {
		    return reportRepository.findByEmployee(employee);
		}


	// 日報保存
	@Transactional
	public ErrorKinds save(Report report, Employee employee) {
		// 注目する従業員と日時を指定して、そのような日報一覧を取得
		List<Report> reportList = reportRepository.findByReportDateAndEmployee(report.getReportDate(), employee);

		// もし、そのような日報がないのであれば return ErrorKinds.DATECHECK_ERROR; を実行する
		if (!reportList.isEmpty()) {
			return ErrorKinds.DATECHECK_ERROR;
		}
		report.setDeleteFlg(false);

		LocalDateTime now = LocalDateTime.now();
		report.setCreatedAt(now);
		report.setUpdatedAt(now);
		report.setEmployee(employee);

		reportRepository.save(report);
		return ErrorKinds.SUCCESS;
	}

	// 日報更新
	@Transactional
	public ErrorKinds update(Report report, Employee employee, Integer id) {
		Report reportBefore = findById(id);

		// その従業員と日付で既に日報が登録されているかチェック
		List<Report> reportList = reportRepository.findByReportDateAndEmployee(report.getReportDate(), employee);

		// 一件でも存在したらエラー
		if (!reportList.isEmpty()) {
			return ErrorKinds.DATECHECK_ERROR;
		}

		// エラーがない場合は更新処理
		report.setId(id);
		report.setDeleteFlg(false);
		report.setCreatedAt(reportBefore.getCreatedAt()); // 元の作成日時を引き継ぐ
		report.setUpdatedAt(LocalDateTime.now());
		report.setEmployee(employee);

		reportRepository.save(report);
		return ErrorKinds.SUCCESS;
	}


	// ★ ここに追加：UserDetailなしのシンプルな削除
	@Transactional
	public void delete(Integer id) {
	    Report report = findById(id);
	    if (report != null) {
	        report.setUpdatedAt(LocalDateTime.now());
	        report.setDeleteFlg(true);
	    }

	}


    // 日報削除
    @Transactional
    public ErrorKinds delete(Integer id, UserDetail userDetail) {


        Report report = findById(id);
        LocalDateTime now = LocalDateTime.now();
         report.setUpdatedAt(now);
        report.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }


}

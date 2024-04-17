package hr.scuric.dewallet.budget.controller;

import hr.scuric.dewallet.budget.enums.ExpenseType;
import hr.scuric.dewallet.budget.enums.Period;
import hr.scuric.dewallet.budget.models.request.ExpenseRequest;
import hr.scuric.dewallet.budget.models.response.ExpenseResponse;
import hr.scuric.dewallet.budget.models.response.ExpenseStatistics;
import hr.scuric.dewallet.budget.service.ExpenseService;
import hr.scuric.dewallet.common.exceptions.DeWalletException;
import hr.scuric.dewallet.common.swagger.OpenApiTags;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/budget")
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping("/expense")
    @Operation(tags = OpenApiTags.EXPENSE, summary = "Insert new expense.")
    public ResponseEntity<ExpenseResponse> registerExpense(@Valid @RequestBody ExpenseRequest request) throws DeWalletException {
        ExpenseResponse response = this.expenseService.insertExpense(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/expenses")
    @Operation(tags = OpenApiTags.EXPENSE, summary = "Get all categories.")
    public ResponseEntity<List<ExpenseResponse>> getCategories() {
        List<ExpenseResponse> response = this.expenseService.getExpenses();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/expense/{id}")
    @Operation(tags = OpenApiTags.EXPENSE, summary = "Get expense by ID.")
    public ResponseEntity<ExpenseResponse> getExpense(@NonNull @PathVariable(name = "id") Long id) throws DeWalletException {
        ExpenseResponse response = this.expenseService.getExpense(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/expense/{id}")
    @Operation(tags = OpenApiTags.EXPENSE, summary = "Update expense.")
    public ResponseEntity<ExpenseResponse> updateExpense(@NonNull @PathVariable(name = "id") Long id, @Valid @RequestBody ExpenseRequest request) throws DeWalletException {
        ExpenseResponse response = this.expenseService.updateExpense(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/expense/{id}/status/{active}")
    @Operation(tags = OpenApiTags.EXPENSE, summary = "Update expense status.")
    public ResponseEntity<ExpenseResponse> updateExpenseStatus(@NonNull @PathVariable(name = "id") Long id, @NonNull @PathVariable(name = "active") Boolean active) throws DeWalletException {
        ExpenseResponse response = this.expenseService.updateExpenseStatus(id, active);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/expense/{id}")
    @Operation(tags = OpenApiTags.EXPENSE, summary = "Delete expense.")
    public ResponseEntity<ExpenseResponse> deleteExpense(@NonNull @PathVariable(name = "id") Long id) throws DeWalletException {
        HttpStatus response = this.expenseService.deleteExpense(id);
        return new ResponseEntity<>(response);
    }

    @GetMapping("/expense/filter")
    @Operation(tags = OpenApiTags.EXPENSE, summary = "Filter expenses.")
    public ResponseEntity<List<ExpenseResponse>> filterExpenses(@RequestParam(name = "categoryId", required = false) Long categoryId,
                                                                @RequestParam(name = "minAmount", required = false) BigDecimal minAmount,
                                                                @RequestParam(name = "maxAmount", required = false) BigDecimal maxAmount,
                                                                @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                                @RequestParam(name = "type", required = false) ExpenseType type) {
        List<ExpenseResponse> response = this.expenseService.filterExpenses(categoryId, minAmount, maxAmount, startDate, endDate, type);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/expense/statistics")
    @Operation(tags = OpenApiTags.EXPENSE, summary = "Statistics.")
    public ResponseEntity<ExpenseStatistics> filterExpenses(@RequestParam(name = "period") Period period,
                                                            @RequestParam(name = "month", required = false) Integer month,
                                                            @RequestParam(name = "year") Integer year,
                                                            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                            @RequestParam(name = "categoryId", required = false) Long categoryId) throws DeWalletException {
        ExpenseStatistics response = this.expenseService.getStatistics(period, month, year, startDate, endDate, categoryId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

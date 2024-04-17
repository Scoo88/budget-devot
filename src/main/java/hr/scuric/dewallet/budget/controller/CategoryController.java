package hr.scuric.dewallet.budget.controller;

import hr.scuric.dewallet.budget.models.request.CategoryRequest;
import hr.scuric.dewallet.budget.models.response.CategoryResponse;
import hr.scuric.dewallet.budget.service.CategoryService;
import hr.scuric.dewallet.common.exceptions.DeWalletException;
import hr.scuric.dewallet.common.swagger.OpenApiTags;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/budget")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping(value = "/category", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(tags = OpenApiTags.CATEGORY, summary = "Insert new category.")
    public ResponseEntity<CategoryResponse> registerCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = this.categoryService.insertCategory(request, Optional.empty());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/categories", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(tags = OpenApiTags.CATEGORY, summary = "Get all categories.")
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        List<CategoryResponse> response = this.categoryService.getCategories();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/category/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(tags = OpenApiTags.CATEGORY, summary = "Get category by ID.")
    public ResponseEntity<CategoryResponse> getCategory(@NonNull @PathVariable(name = "id") Long id) throws DeWalletException {
        CategoryResponse response = this.categoryService.getCategory(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/category/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(tags = OpenApiTags.CATEGORY, summary = "Update category.")
    public ResponseEntity<CategoryResponse> updateCategory(@NonNull @PathVariable(name = "id") Long id, @Valid @RequestBody CategoryRequest request) throws DeWalletException {
        CategoryResponse response = this.categoryService.updateCategory(id, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping(value = "/category/{id}/status/{active}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(tags = OpenApiTags.CATEGORY, summary = "Update category status.")
    public ResponseEntity<CategoryResponse> updateCategoryStatus(@NonNull @PathVariable(name = "id") Long id, @NonNull @PathVariable(name = "active") Boolean active) throws DeWalletException {
        CategoryResponse response = this.categoryService.updateCategoryStatus(id, active);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/category/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(tags = OpenApiTags.CATEGORY, summary = "Delete category.")
    public ResponseEntity<CategoryResponse> deleteCategory(@NonNull @PathVariable(name = "id") Long id) throws DeWalletException {
        HttpStatus response = this.categoryService.deleteCategory(id);
        return new ResponseEntity<>(response);
    }
}

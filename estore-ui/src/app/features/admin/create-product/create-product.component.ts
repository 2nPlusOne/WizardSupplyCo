import { Component, ViewChild, ElementRef } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { InventoryService } from 'src/app/core/services/inventory.service';
import { BaseProduct, Description } from 'src/app/core/model/product.model';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-create-product',
  templateUrl: './create-product.component.html',
  styleUrls: ['./create-product.component.scss']
})
export class CreateProductComponent {
  createProductForm: FormGroup;
  
  @ViewChild('createButtonText', { static: false }) createButtonText!: ElementRef;

  isLoading = false;
  isSuccess = false;

  constructor(
    private fb: FormBuilder,
    private inventoryService: InventoryService,
    private snackBar: MatSnackBar
    ) {
      this.createProductForm = this.fb.group({
        name: ['', [Validators.required]],
        // price is required, and must be a decimal number with 2 decimal places
        price: ['', [Validators.required, Validators.pattern('[0-9]+(\.[0-9][0-9]?)?')]],
        stockQuantity: ['', [Validators.required, Validators.pattern('[0-9]*')]],
        // images is required, and must be a comma-separated list of URL strings
        images: ['', [Validators.pattern('^(https?:\/\/.*\.(?:png|jpg|jpeg|gif|png|svg))(?:, https?:\/\/.*\.(?:png|jpg|jpeg|gif|png|svg))*$')]],
        summary: ['', []],
        // tags is required, and must be a comma-separated list of strings
        tags: ['', [Validators.pattern('^[a-zA-Z0-9]+(?:, [a-zA-Z0-9]+)*$')]],
      });
  }

  ngOnInit(): void { }

  /** Create product form submittion handler */
  async create(): Promise<void> {
    if (!this.createProductForm.valid)
      return;
    
    const { name, price, stockQuantity, images, summary, tags } = this.createProductForm.value;
    const imagesArray: string[] = images ? images.split(', ') : [];
    const tagsArray: string[] = tags ? tags.split(', ') : [];
    const description = new Description(summary, tagsArray);

    const product = new BaseProduct(name, price, stockQuantity, imagesArray, description);
    console.log('Create product form submitted with product:', JSON.stringify(product));

    // call update product on the inventory service
    this.inventoryService.addProduct(product).subscribe({
      next: async (updateSuccess) => {
        if (updateSuccess) {
          console.log('Product updated successfully');
          this.displaySnackBar('Product created successfully', 'Dismiss', 3000);
        } else {
          console.log('Product update failed');
          this.displaySnackBar('Product creation failed', 'Dismiss', 3000, true);
        }
        
        await this.showSubmitFeedback();

        // clear the form
        this.createProductForm.reset();
      },
      error: (e) => {
        console.log('Product update failed with error:', e);
      },
    });
  }

  async showSubmitFeedback(): Promise<void> {
    this.createButtonText.nativeElement.textContent = '';
    this.isLoading = true;
    await new Promise(resolve => setTimeout(resolve, 1000));
    this.isLoading = false;
    this.isSuccess = true;
    await new Promise(resolve => setTimeout(resolve, 1000));
    this.isSuccess = false;
    this.createButtonText.nativeElement.textContent = 'Create';
  }

  /** Display snackbar notification */
  displaySnackBar(message: string, action: string, duration: number, error?: boolean) {
    this.snackBar.open(message, action, {
      duration: duration,
      // panelClass: error ? ['snackbar-error'] : ['snackbar-success']
    });
  }
}

import {Attribute, Directive, forwardRef} from '@angular/core';
import {FormControl, NG_VALIDATORS, Validator} from "@angular/forms";

@Directive({
  selector: '[validateEqual][formControlName],[validateEqual][formControl],[validateEqual][ngModel]',
  providers: [
    { provide: NG_VALIDATORS, useExisting: forwardRef(() => PasswordConfirmValidator), multi: true }
  ]
})
export class PasswordConfirmValidator implements Validator {

  constructor( @Attribute('validateEqual') public validateEqual: string) {

  }

  validate(c: FormControl): { [key: string]: any } {

    // self value (e.g. retype password)
    let v = c.value;

    // control value (e.g. password)
    let e = c.root.get(this.validateEqual);

    // value not equal
    if (e && v !== e.value) return {
      validateEqual: false
    }
    return null;
  }
}

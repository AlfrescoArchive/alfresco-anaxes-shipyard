import { NgModule } from '@angular/core';
import { CoreModule } from 'ng2-alfresco-core';

export function modules() {
  return [ CoreModule ];
}

@NgModule({
  imports: modules(),
  exports: modules()
})
export class AdfModule {}

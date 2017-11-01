import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomHomeComponent } from './customHome.component';

describe('CustomHomeComponent', () => {
  let component: CustomHomeComponent;
  let fixture: ComponentFixture<CustomHomeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ CustomHomeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(CustomHomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});

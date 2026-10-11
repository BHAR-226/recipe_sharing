import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecipeEditPage } from './recipe-edit-page';

describe('RecipeEditPage', () => {
  let component: RecipeEditPage;
  let fixture: ComponentFixture<RecipeEditPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecipeEditPage],
    }).compileComponents();

    fixture = TestBed.createComponent(RecipeEditPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

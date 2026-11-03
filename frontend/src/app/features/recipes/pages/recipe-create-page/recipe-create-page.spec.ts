import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecipeCreatePage } from './recipe-create-page';

describe('RecipeCreatePage', () => {
  let component: RecipeCreatePage;
  let fixture: ComponentFixture<RecipeCreatePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecipeCreatePage],
    }).compileComponents();

    fixture = TestBed.createComponent(RecipeCreatePage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

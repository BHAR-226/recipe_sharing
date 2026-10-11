import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecipeActions } from './recipe-actions';

describe('RecipeActions', () => {
  let component: RecipeActions;
  let fixture: ComponentFixture<RecipeActions>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecipeActions],
    }).compileComponents();

    fixture = TestBed.createComponent(RecipeActions);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

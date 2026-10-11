import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecipeComments } from './recipe-comments';

describe('RecipeComments', () => {
  let component: RecipeComments;
  let fixture: ComponentFixture<RecipeComments>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecipeComments],
    }).compileComponents();

    fixture = TestBed.createComponent(RecipeComments);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

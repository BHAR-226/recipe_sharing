import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecipeDetailPage } from './recipe-detail-page';

describe('RecipeDetailPage', () => {
  let component: RecipeDetailPage;
  let fixture: ComponentFixture<RecipeDetailPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecipeDetailPage],
    }).compileComponents();

    fixture = TestBed.createComponent(RecipeDetailPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

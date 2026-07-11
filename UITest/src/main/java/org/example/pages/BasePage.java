package org.example.pages;

import com.codeborne.selenide.Selenide;

public abstract class BasePage<T extends BasePage<T>> {
  public BasePage() {
    Selenide.page(this);
  }

  public abstract T waitForPageLoaded();
}

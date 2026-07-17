package org.example.components;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.$$x;

public class CommentSection {
  private static final Logger log = LoggerFactory.getLogger(CommentSection.class);

  @FindBy(xpath = "//div[@data-test='wysiwyg-editor']//div[@contenteditable='true']")
  private SelenideElement commentInput;
  @FindBy(xpath = "//div[@data-test='editor-actions']//button[@data-test='post-comment']")
  private SelenideElement commentPostButton;
  @FindBy(xpath = "//div[@data-test='ring-popup']//button[contains(@id, ':remove')]")
  private SelenideElement commentRemove;

  public CommentSection() {
    Selenide.page(this);
  }

  public CommentSection addComment(String text) {
    commentInput.shouldBe(Condition.visible, Condition.enabled).setValue(text);
    commentPostButton.shouldBe(Condition.visible, Condition.enabled).click();
    return this;
  }

  public CommentSection openMoreMenuForComment(String commentText) {
    try {
      SelenideElement commentBlock = $$x("//div[@data-test='change-item']")
          .filterBy(Condition.text(commentText))
          .first();
      commentBlock.$x(".//button[@data-test='comment-menu']").click();
    } catch (AssertionError e) {
      log.error("Проблема с комментарием: {}", e.getMessage());
    }
    return this;
  }

  public boolean checkCommentExists(String commentText) {
    try {
      $$x("//div[@data-test='change-item']")
          .filterBy(Condition.text(commentText))
          .shouldHave(CollectionCondition.sizeGreaterThan(0));
      return true;
    } catch (AssertionError e) {
      return false;
    }
  }

  public void deleteComment(String commentText) {
    openMoreMenuForComment(commentText);
    commentRemove.click();
  }
}

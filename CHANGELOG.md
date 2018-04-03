# Android XDK

## 4.0

Initial XDK stable release

### Migration Guide
  * Please refer to the Migration Guide from Atlas to the XDK on https://docs.layer.com

## 1.0.0-pre2

### Features
  * Updated Layer SDK to `1.0.0-pre2`
  * Updated support libraries to `27.1.0`
  * Added empty view to the message items list
  
### Bug Fixes
  * Improved performance of the message items list (AND-1242)

### Migration Guide
  * Remove calls to `ConversationView.onDestroy()`. It is no longer needed to explicitly destroy that view.
  * The `CellFactory` concept is no more. Existing custom cell factories will need to be converted to the new `MessageModel` format.
    - Override the `MessageModel.processLegacyParts()` method to handle `MessagePart` parsing.
    - Supply view and container view resources as you would a custom `MessageModel`.
    - Existing mime type constants can be found in `ui.message.LegacyMimeTypes`.
  * Item click listeners have been separated
    - Single and long click listeners are now two separate classes. Move the handling on `onItemLongClick` in the old `OnItemClickListener` to a new `OnItemLongClickListener` and register that on the view model with `setItemLongClickListener()`.
  * Remove the cell factory collection from the instantiation of any `ConversationViewModel` and `ConversationItemFormatter` instances.
  * The `MessageItemListView` style has been removed.
  * `MessageModel`s have been slightly re-worked.
    - They now define layout IDs instead of class names for the view to use.
    - They also define the container layout ID instead of relying on the view to supply this.
  * Queries can no longer be provided to a `ConversationView`. A predicate can be set on the `MessageItemsListViewModel` by calling the `setQueryPredicate(Predicate)` method.
  * `MessagesAdapter` has been removed. Any extension of this should now extend from `MessageModelAdapter`.

## 1.0.0-pre1

Initial XDK preview.
# Android XDK

## 4.1.0-pre1

### Features
  * Audio Message support (AND-1436)
  * Video Message support (AND-1441)
  * Feedback Message support (AND-1442)
  * Analytics event support (AND-1460)
  * Updated Layer SDK to `4.1.0-pre1`
  * Added dependency management singleton for dependency injection. See `XdkUiDependencyManager`. (AND-1468)
  * Sending the full `MessageModel` when performing message actions (AND-1471)
  * Added default support for the `layer-show-large-message` message action

### Bug fixes
  * Only loading a resource ID in `PicassoImageCacheWrapper` if one is set. If not, fall back to the placeholder.
  * Using correct black color for `xdk_ui_text_black`

### API Changes
  * `XDKUiComponent` added two `inject()` methods for large message `ViewModel` injection
  * `ConversationViewModel` should now register with a `LifecycleOwner` via `addLifecycleObservers(LifecycleOwner)`
  * `ActionHandler`'s `performAction()` method now sends the `MessageModel` instead of a `JsonObject`. Custom `ActionHandler`s will need to be updated. Use `MessageModel.getActionData()` to retrieve the `JsonObject` that used to be passed through.
  * `MessageModelAdapter` now takes an additional constructor parameter for media playback support
  * `ChoiceOrSetHelper` now extends the abstract `ORSetHelper`, which holds the common functionality for working with OR-sets.
  * `MessageModel`'s `processParts(MessagePart)` method is now public instead of protected
  * `ParentMessageView` will now pass a `MediaControllerProivder` when inflating views. The signature of `inflateChildLayouts()` for any custom `ParentMessageView` will need to be updated.
  * `MessageModel.getRootMessagePart()` is now nullable. This can be null if the model has not been parsed yet or if it is a legacy MIME type.

### New APIs
  * A message view can implement `MediaMessageView`... (see MessageModelAdapater)
  * Added a `ViewStub` to the right of the metadata in the `StandardMessageContainer`. See `getRightMetadataView()`.
  * Added a download progress accessor to `MessageModel`. See `getDownloadProgress()`.
  * Added a convenience method to send analytics events from a `MessageModel`. See `postAnalyticsEvent(LayerAnalyticsEvent)`.
  * Added a `MediaPlayerMessageView` interface that message views can implement. This allows an adapter to set a media controller on the view to support media playback.
  * Added a `MessagePartFetcher` that will fetch a `MessagePart` from the `LayerClient` and observe changes.

## 4.0.6

### Features
  * Updated Layer SDK to `4.0.6`
  
## 4.0.5

### Bug Fixes
  * Fixing crash when an announcement is synced and the `ConversationView` is displayed (AND-1452)
  * Ensuring intents can be resolved before starting activities in the built-in action handlers (AND-1472)

## 4.0.4

### Features
  * Updated Layer SDK to `4.0.4`

### Bug Fixes
  * Removed the constrained height on the compose bar so it will expand to the maximum specified lines (AND-1453)
  * Compose bar's `EditText` now auto capitalizes sentences
  * Dismissing the attachment menu after a selection was made (AND-1454)

## 4.0.3

### Important Changes
  * Added performance test module
  * SDK pre-release URL is now set via a variable in `gradle.properties`
  * Common dependency versions have moved to the root `build.gradle` file
  
### Bug Fixes
  * Properly handling the `DefaultXdkUiComponent` singleton inside the `ServiceLocator`
  * No longer using a weak `LayerAuthenticationListener` during deauthentication as it would usually be GC'd and the specified `DeauthenticationCallback` would not be called.
  * Fixed crash when selecting choice buttons with different button count (AND-1451)
  
## 4.0.2

### Features
  * Updated Layer SDK to `4.0.2`
  
## 4.0.1

### Features
  * Improved unit test foundations (AND-1418)
  * Updated to use 4.0.1 version of the SDK
  * Removed TestButler as it was going unused

### Bug Fixes
  * Added a default push notification payload to the rich text sender (AND-1430) 

## 4.0.0

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
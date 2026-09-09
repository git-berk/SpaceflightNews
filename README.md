# Spaceflight News

An Android news reader for spaceflight articles. Browse a paginated feed, search,
save favourites, and read the full story in the publisher's own page.

Articles come from the public
[Spaceflight News API](https://api.spaceflightnewsapi.net/v4/) (SNAPI), which
aggregates headlines from NASA, NASASpaceflight, ESA and others.

## Tech stack

| | |
|---|---|
| Language | Kotlin 2.2.10 |
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM, unidirectional state |
| DI | Hilt 2.56 |
| Network | Retrofit 2.12 + OkHttp 4.12, kotlinx.serialization |
| Local storage | Room 2.8 |
| Paging | Paging 3.5 with a `RemoteMediator` |
| Images | Coil 3.2 |
| Navigation | Navigation Compose, type-safe routes |
| Build | AGP 8.13, minSdk 26, targetSdk 36 |

## Modules

```
:core:model   Kotlin/JVM. Article, AppError. No Android, no third-party deps.
:core:data    Network, Room, paging, repositories, DI.
:core:ui      Design system: theme, components, previews.
:app          Screens, ViewModels, navigation.
```

`:core:model` is a plain JVM module so the domain types stay free of the
framework. `:core:data` owns everything about *where* data comes from; nothing
above it imports Retrofit, Room or OkHttp. Failures arrive as `AppError`.

## Navigation

Each screen owns its own route and its own `NavGraphBuilder` extension:

Routes are type-safe `@Serializable` objects, so arguments are passed as data
rather than encoded into strings. A screen exposes a `navigateToX()` function
and a `xScreen()` graph builder, and nothing else. No screen knows about any
other screen's route. Moving one into a `:feature:*` module is a file move plus
a Gradle line. 

The graph is two levels:

```
SpaceflightNavHost
├── HomeRoute            bottom bar / navigation rail + tab graph
│   ├── FeedRoute
│   ├── SearchRoute      keeps the bottom bar; back returns to the feed
│   └── FavoritesRoute
└── ArticleDetailRoute   sibling of Home
```


## Paging and caching

The feed is offline-first. Room is the single source of truth; the network only
tops it up.

```
API ──► ArticleRemoteMediator ──► Room ──► PagingSource ──► UI
```

- **Page size** 20, with the first load covering 3 pages.
- **Cache TTL is 10 minutes.** On launch the mediator compares the stored
  timestamp against the clock: inside the window it skips the network and serves
  Room; outside it refreshes. Pull-to-refresh always refreshes.
- **Pagination is pinned.** Each session sends `published_at_lte` with a fixed
  timestamp, so articles published mid-scroll cannot shift the offset window and
  make a page repeat rows.
- **A failed refresh keeps the cache.** The old rows are only cleared inside the
  transaction that writes the new ones, so going offline leaves the feed intact
  behind a banner.
- **Favourites are stored separately** from the feed cache, which is wiped on
  every refresh, and layered onto the paged data in the ViewModel.

Search is network-only and has no cache.

## Tests

87 unit tests: 50 in `:core:data`, 29 in `:app`, 8 in `:core:ui`.

```bash
./gradlew test
```

They cover the parts most likely to break silently: mediator TTL and snapshot
pinning, that a failed refresh preserves the cache, that a favourite toggle does
not invalidate the feed, search debouncing, and the detail screen's
loading/content/not-found states. Room tests run against an in-memory database
under Robolectric.

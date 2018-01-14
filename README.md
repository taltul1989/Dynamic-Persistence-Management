# Dynamic-Persistence-Management

I created a project that add/update/remove entity objects into cache and db (a file will be my db).

When a user decide to add a specific entity (Example: Person or Vehicle), The cache will only add this specific entity.

There are 2 functionalities for cache - lazy loading or eager loading.

In case of Eager Loading - the cache will add all entities from current db in init.

In case of Lazy Loading - the cache will be empty in init.




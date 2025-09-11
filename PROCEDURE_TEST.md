# 📋 Procédure de test - Travel Guide App

> **Guide complet pour évaluer l'application Travel Guide lors du test technique**

## 🚀 Démarrage de l'application

### 1. Prérequis
- Java 21+ installé
- Node.js 18+ installé
- Git

### 2. Installation et lancement

```bash
# Cloner le repository
git clone <repository-url>
cd travel

# Terminal 1 - Backend (Spring Boot)
cd backend
./mvnw spring-boot:run
# Attendre le message "Started TravelApplication in X seconds"

# Terminal 2 - Frontend (Angular) 
cd frontend
npm install
ng serve
# Attendre "Application bundle generation complete"
```

### 3. URLs d'accès
- **Application** : http://localhost:4200
- **API Swagger** : http://localhost:8080/swagger-ui/index.html
- **Console H2** : http://localhost:8080/h2-console

---

## 🧪 Scénarios de test

### Scénario 1 : Authentification et rôles

#### Test 1.1 - Connexion Administrateur
1. Aller sur http://localhost:4200
2. Se connecter avec :
   - **Email** : `admin@admin.com`
   - **Mot de passe** : `admin123`
3. **✅ Résultat attendu** : Accès à tous les guides + fonctions d'administration

#### Test 1.2 - Connexion Utilisateur standard
1. Se déconnecter et se reconnecter avec :
   - **Email** : `user1@test.com`
   - **Mot de passe** : `admin123`
2. **✅ Résultat attendu** : Accès limité aux guides "Paris Culturel" et "Paris Vert" uniquement

#### Test 1.3 - Vérification des permissions
1. Connecté en tant que `user1@test.com`
2. Essayer d'accéder aux fonctions d'administration
3. **✅ Résultat attendu** : Accès refusé, redirection ou erreur 403

### Scénario 2 : Gestion des guides

#### Test 2.1 - Consultation des guides (Admin)
1. Se connecter en tant qu'admin (`admin@admin.com`)
2. Naviguer vers la liste des guides
3. **✅ Résultat attendu** : Voir les 3 guides :
   - "Paris Culturel - 3 jours"
   - "Châteaux de la Loire - 5 jours" 
   - "Paris Vert - 2 jours"

#### Test 2.2 - Détail d'un guide avec activités
1. Cliquer sur "Paris Culturel"
2. **✅ Résultat attendu** : Voir le guide avec ses activités organisées par jour :
   - **Jour 1** : Musée du Louvre, Tour Eiffel
   - **Jour 2** : Musée d'Orsay
   - **Jour 3** : Château de Versailles

#### Test 2.3 - Propriétés des guides
1. Vérifier que chaque guide affiche :
   - **Mobilité** (Voiture, Transport public, etc.)
   - **Saisons** (Printemps, Été, etc.)
   - **Pour qui** (Famille, Couple, etc.)
2. **✅ Résultat attendu** : Toutes les propriétés sont renseignées

### Scénario 3 : Tests API avec Postman

#### Test 3.1 - Documentation Swagger (Consultation)
1. Aller sur http://localhost:8080/swagger-ui/index.html
2. **✅ Résultat attendu** : Interface Swagger UI avec tous les endpoints documentés
3. **Note** : Swagger sert ici uniquement de documentation, les tests se font avec Postman

#### Test 3.2 - Authentification et autorisation avec POSTMAN

**Test 3.2.1 - Login Admin**
1. **POST** `http://localhost:8080/api/auth/login`
   - Body (JSON) : `{"email":"admin@admin.com","password":"admin123"}`
   - **✅ Attendu** : Status 200 + token JWT
2. **Copier le token JWT** pour les tests suivants

**Test 3.2.2 - Login User standard**
1. **POST** `http://localhost:8080/api/auth/login`
   - Body (JSON) : `{"email":"user1@test.com","password":"admin123"}`
   - **✅ Attendu** : Status 200 + token JWT différent
2. **Copier ce token** pour les tests de restrictions

**Test 3.2.3 - Login invalide**
1. **POST** `http://localhost:8080/api/auth/login`
   - Body (JSON) : `{"email":"wrong@email.com","password":"wrong"}`
   - **✅ Attendu** : Status 401 Unauthorized

#### Test 3.3 - Endpoints Guides (Admin)

**Utiliser le token admin dans tous ces tests : `Authorization: Bearer TOKEN_ADMIN`**

**Test 3.3.1 - Liste complète des guides (Admin uniquement)**
1. **GET** `http://localhost:8080/api/guides`
   - Header : `Authorization: Bearer TOKEN_ADMIN`
   - **✅ Attendu** : Status 200 + liste des 3 guides
   - **Vérifier** : Présence de "Paris Culturel", "Châteaux de la Loire", "Paris Vert"

**Test 3.3.2 - Détail d'un guide avec activités**
1. **GET** `http://localhost:8080/api/guides/1`
   - Header : `Authorization: Bearer TOKEN_ADMIN`
   - **✅ Attendu** : Status 200 + détail du guide avec activités
   - **Vérifier** : Présence des activités organisées par jour/ordre

**Test 3.3.3 - Guide inexistant**
1. **GET** `http://localhost:8080/api/guides/999`
   - Header : `Authorization: Bearer TOKEN_ADMIN`
   - **✅ Attendu** : Status 404 Not Found

**Test 3.3.4 - Sans authentification**
1. **GET** `http://localhost:8080/api/guides`
   - **Pas de header Authorization**
   - **✅ Attendu** : Status 401 Unauthorized

#### Test 3.4 - Restrictions utilisateur standard

**Utiliser le token user1 : `Authorization: Bearer TOKEN_USER1`**

**Test 3.4.1 - Accès refusé à la liste complète**
1. **GET** `http://localhost:8080/api/guides`
   - Header : `Authorization: Bearer TOKEN_USER1`
   - **✅ Attendu** : Status 403 Forbidden (user1 n'est pas admin)

**Test 3.4.2 - Guides accessibles à user1**
1. **GET** `http://localhost:8080/api/guides/mes-guides`
   - Header : `Authorization: Bearer TOKEN_USER1`
   - **✅ Attendu** : Status 200 + seulement 2 guides ("Paris Culturel" et "Paris Vert")

**Test 3.4.3 - Accès à un guide autorisé**
1. **GET** `http://localhost:8080/api/guides/1` (Paris Culturel)
   - Header : `Authorization: Bearer TOKEN_USER1`
   - **✅ Attendu** : Status 200 + détail du guide

**Test 3.4.4 - Accès refusé à un guide non autorisé**
1. **GET** `http://localhost:8080/api/guides/2` (Châteaux de la Loire - non accessible à user1)
   - Header : `Authorization: Bearer TOKEN_USER1`
   - **✅ Attendu** : Status 403 Forbidden

#### Test 3.5 - Endpoints Activités

**Utiliser le token admin : `Authorization: Bearer TOKEN_ADMIN`**

**Test 3.5.1 - Liste des activités**
1. **GET** `http://localhost:8080/api/activites`
   - Header : `Authorization: Bearer TOKEN_ADMIN`
   - **✅ Attendu** : Status 200 + liste des 8 activités
   - **Vérifier** : Catégories (MUSEE, CHATEAU, PARC, GROTTE, ACTIVITE)

**Test 3.5.2 - Détail d'une activité**
1. **GET** `http://localhost:8080/api/activites/1`
   - Header : `Authorization: Bearer TOKEN_ADMIN`
   - **✅ Attendu** : Status 200 + détail complet (titre, description, adresse, horaires, etc.)

#### Test 3.6 - Gestion des utilisateurs (Admin uniquement)

**Test 3.6.1 - Liste des utilisateurs**
1. **GET** `http://localhost:8080/api/users`
   - Header : `Authorization: Bearer TOKEN_ADMIN`
   - **✅ Attendu** : Status 200 + liste des utilisateurs

**Test 3.6.2 - Accès refusé pour user standard**
1. **GET** `http://localhost:8080/api/users`
   - Header : `Authorization: Bearer TOKEN_USER1`
   - **✅ Attendu** : Status 403 Forbidden

#### Test 3.7 - Validation des données

**Test 3.7.1 - Guide avec ID invalide**
1. **GET** `http://localhost:8080/api/guides/-1`
   - Header : `Authorization: Bearer TOKEN_ADMIN`
   - **✅ Attendu** : Status 400 Bad Request (validation)

**Test 3.7.2 - Token JWT expiré/invalide**
1. **GET** `http://localhost:8080/api/guides`
   - Header : `Authorization: Bearer INVALID_TOKEN`
   - **✅ Attendu** : Status 401 Unauthorized

#### Test 3.8 - Endpoints publics

**Test 3.8.1 - Health check**
1. **GET** `http://localhost:8080/api/health`
   - **Pas d'authentification**
   - **✅ Attendu** : Status 200 + info système

**Test 3.8.2 - Documentation OpenAPI**
1. **GET** `http://localhost:8080/v3/api-docs`
   - **Pas d'authentification**
   - **✅ Attendu** : Status 200 + JSON de documentation API

### Scénario 4 : Vérification des activités dans les guides

#### Test 4.1 - Activités dans "Paris Culturel"
1. Connecté en admin, cliquer sur le guide "Paris Culturel"
2. **✅ Résultat attendu** : Voir les activités organisées par jour :
   - **Jour 1** : Musée du Louvre (ordre 1), Tour Eiffel (ordre 2)
   - **Jour 2** : Musée d'Orsay (ordre 1)
   - **Jour 3** : Château de Versailles (ordre 1)

#### Test 4.2 - Activités dans "Châteaux de la Loire"
1. Cliquer sur le guide "Châteaux de la Loire"
2. **✅ Résultat attendu** : Voir les activités avec les bonnes catégories :
   - **Jour 1** : Château de Chambord (CHATEAU)
   - **Jour 2** : Château de Versailles (CHATEAU)
   - **Jour 3** : Grottes de Lascaux IV (GROTTE)

#### Test 4.3 - Informations complètes des activités
1. Dans le détail d'un guide, vérifier qu'une activité affiche :
   - Titre, description, adresse
   - Horaires d'ouverture, téléphone
   - Site internet, catégorie
2. **✅ Résultat attendu** : Toutes les informations sont présentes et correctes

### Scénario 5 : Mode hors-ligne (PWA)

#### Test 5.1 - Cache hors-ligne 
1. Naviguer dans l'application (consulter plusieurs guides et leurs activités)
2. **Couper internet** : 
   - Windows : Désactiver WiFi ou débrancher Ethernet
   - Ou dans Chrome : F12 → onglet "Network" → cocher "Offline"
3. Recharger la page (F5) ou fermer/rouvrir l'onglet
4. **✅ Résultat attendu** : L'application et les guides consultés restent accessibles
5. **✅ Utilité** : Permet de consulter les guides de voyage même sans connexion internet

---


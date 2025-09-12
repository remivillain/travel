# 🧳 Travel Guide App

Application de gestion de guides de voyage avec système d'invitations et support hors-ligne.

## 🚀 Démarrage rapide

### Option 1 : Avec Docker (Recommandé - Plus simple)

#### Prérequis
- Docker et Docker Compose installés
- Git

#### Lancement
```bash
# Cloner le repository
git clone <repository-url>
cd travel

# Lancer l'application complète
docker-compose up --build

# Attendre les messages de démarrage :
# ✔ Container travel-backend-1   Started
# ✔ Container travel-frontend-1  Started
```

** Note importante** : Si vous avez des erreurs de port (8080 ou 4200 déjà utilisés), arrêtez vos services locaux ou modifiez les ports dans `docker-compose.yml`.

### Option 2 : Installation locale (Développement)

#### Prérequis
- Java 21+ installé
- Node.js 18+ installé
- Git

#### Lancement
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

### ⚠️ Dépannage installation Frontend

**En cas d'erreurs lors de `npm install` :**

```bash
# Dans le dossier frontend/
# 1. Supprimer les fichiers
rm -f package-lock.json
rm -rf node_modules

# 2. Réinstaller
npm install
```

**Note** : Si `node_modules` résiste à la suppression, utiliser `npm install --force`

L'application sera accessible sur :
- Frontend : http://localhost:4200
- Backend API : http://localhost:8080
- Console H2 : http://localhost:8080/h2-console
- **Documentation API (Swagger)** : http://localhost:8080/swagger-ui/index.html

## 🔑 Comptes de test

**Mot de passe pour tous les comptes : `admin123`**

### 👨‍💼 Administrateur
- **Email** : `admin@admin.com`
- **Rôle** : ADMIN
- **Accès** : Tous les guides + fonctions d'administration 

### 👤 Utilisateurs standard

1. **User 1**
   - **Email** : `user1@test.com`
   - **Rôle** : USER
   - **Accès** : Guide "Paris Culturel" + Guide "Paris Vert"

2. **User 2**
   - **Email** : `user2@test.com`
   - **Rôle** : USER
   - **Accès** : Guide "Châteaux de la Loire" + Guide "Paris Vert"

3. **Guide User**
   - **Email** : `guide@test.com`
   - **Rôle** : USER
   - **Accès** : Tous les guides (Paris Culturel, Châteaux de la Loire, Paris Vert)

## 📚 Données de test créées automatiquement

### Guides disponibles
1. **"Paris Culturel - 3 jours"** - Découverte des principaux musées et monuments parisiens
2. **"Châteaux de la Loire - 5 jours"** - Circuit découverte des plus beaux châteaux de la Loire
3. **"Paris Vert - 2 jours"** - Les plus beaux parcs et jardins de Paris

### Activités disponibles
- **Musées** : Louvre, Musée d'Orsay
- **Châteaux** : Versailles, Chambord
- **Monuments** : Tour Eiffel
- **Parcs** : Buttes-Chaumont, Luxembourg
- **Sites naturels** : Grottes de Lascaux IV

## 🛠️ Fonctionnalités

### ✅ Gestion des guides
- Consultation des guides par utilisateur invité
- Administration complète pour les admins
- Système d'invitations par ID utilisateur

### ✅ Gestion des activités
- CRUD complet des activités
- Catégorisation (Musée, Château, Parc, Grotte)
- Informations pratiques complètes

### ✅ Système d'authentification
- JWT avec rôles (ADMIN/USER)
- Contrôle d'accès basé sur les invitations
- Sécurisation de toutes les routes

### ✅ Mode hors-ligne (PWA)
- Cache intelligent des données
- Synchronisation automatique
- Interface adaptative
- Support Service Worker

## 📖 Documentation API

### Swagger UI (SpringDoc OpenAPI 3)
La documentation interactive de l'API est disponible via SpringDoc OpenAPI 3 :
- **URL principale** : http://localhost:8080/swagger-ui/index.html
- **URL alternative** : http://localhost:8080/swagger-ui.html
- **OpenAPI JSON** : http://localhost:8080/v3/api-docs
- **Version SpringDoc** : 2.8.13 (compatible Spring Boot 3.5.5)


### Endpoints documentés avec annotations OpenAPI 3
- **Authentification** : Connexion et gestion des tokens JWT
- **Guides** : CRUD complet + système d'invitations entre utilisateurs  
- **Activités** : Gestion des activités touristiques par catégorie
- **Utilisateurs** : Administration des comptes (ADMIN uniquement)
- **Rôles** : Gestion des rôles système (ADMIN, USER, etc.)


## 📱 Mode hors-ligne

Consultez le [Guide du mode hors-ligne](OFFLINE_GUIDE.md) pour plus de détails sur les fonctionnalités PWA.

## 🏗️ Architecture

### Backend
- **Spring Boot 3.5.5** avec Spring Security
- **SpringDoc OpenAPI 3** (v2.8.13) pour la documentation API
- **JPA/Hibernate** avec base H2 (dev)
- **JWT** pour l'authentification
- **Bean Validation** pour la validation des données

### Frontend
- **Angular 19** avec TypeScript
- **Standalone Components** 
- **PWA** avec Service Worker
- **Tailwind CSS** pour le style

### 🔧 Versions et compatibilité
- **Java** : 21+ 
- **Spring Boot** : 3.5.5
- **SpringDoc OpenAPI** : 2.8.13 (compatible Spring Boot 3.5.x)
- **Maven** : 3.8+
- **Node.js** : 18+
- **Angular** : 19

## 📈 Processus de développement

### Phase 1 : Backend Foundation
- Création des entités, repositories et services
- Configuration H2 et architecture Spring Boot
- Mise en place JWT et système d'authentification

### Phase 2 : Features Core
- CRUD complet guides et activités
- Système d'invitations utilisateur
- Contrôle d'accès et permissions

### Phase 3 : Frontend & API
- Développement Angular avec Standalone Components
- Intégration Tailwind CSS et composants UI
- Connexion API REST avec gestion JWT

### Phase 4 : UX & Performance
- Mode hors-ligne avec PWA
- Animations et spinners
- Filtres et optimisations UI

### Phase 5 : Testing & Security
- Tests d'intégration complets
- Correction offline
- Procédures de test

### Phase 6 : DevOps & Deploy
- Configuration Docker complète
- Documentation API Swagger
- Procédures de déploiement

## 📊 Base de données

### Console H2 (développement)
- URL : http://localhost:8080/h2-console
- JDBC URL : `jdbc:h2:mem:testdb`
- Username : `sa`
- Password : *(vide)*

## 🔧 Configuration

### Ports par défaut
- Backend : 8080
- Frontend : 4200

### Profils Spring
- `default` : Base H2 en mémoire
- Production : PostgreSQL (à configurer)

## 📝 Endpoints API principaux

### Authentification
- `POST /api/auth/login` - Connexion
- `POST /api/auth/register` - Inscription

### Guides
- `GET /api/guides` - Liste tous les guides (ADMIN)
- `GET /api/guides/mes-guides` - Guides de l'utilisateur
- `GET /api/guides/{id}` - Détail d'un guide
- `POST /api/guides/{id}/invite` - Inviter un utilisateur (ADMIN)
- `DELETE /api/guides/{id}/invite` - Retirer un utilisateur (ADMIN)

### Rôles
- `GET /api/roles` - Liste des rôles (ADMIN)
- `GET /api/roles/{id}` - Détail d'un rôle (ADMIN)
- `POST /api/roles` - Créer un rôle (ADMIN)
- `DELETE /api/roles/{id}` - Supprimer un rôle (ADMIN)

## Une procédure de test est disponible ici : (PROCEDURE_TEST.md)


    package com.example.roombookingapp.data.model;
    
    import java.util.Objects;
    
    public class Room {
        private String id;
        private String name;
        private int capacity;
        private String description;

        private String status;
        private String bookedBy;

        private String imageUrl;

        public Room() {}
    
        public Room(String id, String name, int capacity, String description) {
            this.id = id;
            this.name = name;
            this.capacity = capacity;
            this.description = description;
            this.status="available";
            this.bookedBy = "";
            this.imageUrl = "";
        }
    
        // 2. Keep your Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public int getCapacity() { return capacity; }
        public String getDescription() { return description; }

        public String getStatus() {
            return status;
        }

        public String getBookedBy() {
            return bookedBy;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public void setBookedBy(String bookedBy) {
            this.bookedBy = bookedBy;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public void setId(String id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setCapacity(int capacity) { this.capacity = capacity; }
        public void setDescription(String description) { this.description = description; }
    
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Room room = (Room) o;
            return Objects.equals(id, room.id);
        }
    
        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
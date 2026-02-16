const app = Vue.createApp({

    /*
    ============================================
    APPLICATION STATE (Reactive Data)
    ============================================
    */
    data() {
        return {

            // Backend base URL (Spring Boot API)
            apiUrl: 'https://intern-management-backend-kubj.onrender.com/',

            // Controls which section is visible: 'list' or 'form'
            currentView: 'list',

            // Flag to determine whether we are editing or creating
            isEditing: false,

            // Stores randomly selected intern for Coffee Roulette
            luckyIntern: null,

            // Array holding all interns fetched from backend
            interns: [],

            // Form model bound to inputs
            form: {
                id: null,
                name: '',
                email: '',
                department: ''
            },

            /*
            Backend validation errors.
            Each key matches a field name from backend response.
            Example:
            {
                "email": "Email already exists"
            }
            */
            errors: {
                name: '',
                email: '',
                department: ''
            },

            /*
            Delete Confirmation Modal State
            */
            showDeleteModal: false,   // Controls visibility
            internToDelete: null,     // Stores intern selected for deletion

            /*
            Coffee Roulette Empty-State Modal
            */
            showNoInternModal: false  // Shown when no interns exist
        };
    },

    /*
    ============================================
    LIFECYCLE HOOK
    Runs automatically when component mounts
    ============================================
    */
    mounted() {
        this.fetchInterns(); // Load interns when page loads
    },

    /*
    ============================================
    METHODS
    ============================================
    */
    methods: {

        /*
        Switch between list view and form view
        Also resets special states when switching
        */
        changeView(viewName) {
            this.currentView = viewName;

            // Reset Coffee Roulette display when switching views
            this.luckyIntern = null;

            // If navigating to form for new entry
            if (viewName === 'form') {
                this.resetForm();
                this.isEditing = false;
                this.clearErrors();
            }
        },

        /*
        Fetch all interns from backend (GET /interns)
        */
        async fetchInterns() {
            try {
                const response = await fetch(`${this.apiUrl}interns`);
                this.interns = await response.json();
            } catch (error) {
                console.error("Error fetching interns:", error);
            }
        },

        /*
        Save or Update intern
        - POST for new intern
        - PUT for editing existing intern
        Handles backend validation errors
        */
        async saveIntern() {

            // Clear old validation errors before submission
            this.clearErrors();

            try {
                let url = `${this.apiUrl}newIntern`;
                let method = 'POST';

                // If editing, switch to PUT endpoint
                if (this.isEditing) {
                    url = `${this.apiUrl}${this.form.id}`;
                    method = 'PUT';
                }

                const response = await fetch(url, {
                    method: method,
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(this.form)
                });

                // If backend returns validation errors (400)
                if (!response.ok) {
                    const errorData = await response.json();

                    // Map backend errors to frontend error object
                    Object.keys(errorData).forEach(key => {
                        if (this.errors.hasOwnProperty(key)) {
                            this.errors[key] = errorData[key];
                        }
                    });

                    return; // Stop execution if validation fails
                }

                // If successful → refresh list and return to list view
                await this.fetchInterns();
                this.changeView('list');

            } catch (error) {
                console.error("Error saving intern:", error);
            }
        },

        /*
        Prepare form for editing
        Copies selected intern data into form
        */
        prepareEdit(intern) {
            this.form = { ...intern }; // Spread copy
            this.isEditing = true;
            this.currentView = 'form';
            this.clearErrors();
        },

        /*
        ============================================
        DELETE MODAL LOGIC
        ============================================
        */

        // Open confirmation modal
        openDeleteModal(intern) {
            this.internToDelete = intern;
            this.showDeleteModal = true;
        },

        // Close confirmation modal
        closeDeleteModal() {
            this.showDeleteModal = false;
            this.internToDelete = null;
        },

        // Confirm deletion and call backend
        async confirmDelete() {
            try {
                await fetch(`${this.apiUrl}eraseIntern/${this.internToDelete.id}`, {
                    method: 'DELETE'
                });

                // Refresh list after deletion
                await this.fetchInterns();

                // Close modal
                this.closeDeleteModal();

            } catch (error) {
                console.error("Error deleting intern:", error);
            }
        },

        /*
        ============================================
        COFFEE ROULETTE EMPTY MODAL LOGIC
        ============================================
        */

        // Close "No Interns" modal
        closeNoInternModal() {
            this.showNoInternModal = false;
        },

        // Navigate directly to Add Intern form
        goToAddIntern() {
            this.showNoInternModal = false;
            this.changeView('form');
        },

        /*
        ============================================
        COFFEE ROULETTE LOGIC
        ============================================
        */

        runCoffeeRoulette() {

            // If no interns exist → show modal
            if (this.interns.length === 0) {
                this.showNoInternModal = true;
                return;
            }

            // Pick random intern index
            const randomIndex = Math.floor(Math.random() * this.interns.length);

            // Store selected intern
            this.luckyIntern = this.interns[randomIndex];
        },

        /*
        ============================================
        HELPER METHODS
        ============================================
        */

        // Reset form fields
        resetForm() {
            this.form = {
                id: null,
                name: '',
                email: '',
                department: ''
            };
        },

        // Clear validation errors
        clearErrors() {
            this.errors = {
                name: '',
                email: '',
                department: ''
            };
        }
    }
});

// Mount Vue app to #app div
app.mount('#app');

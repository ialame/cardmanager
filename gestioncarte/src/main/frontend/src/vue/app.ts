import {createApp} from 'vue';
import {createRouter, createWebHistory} from "vue-router";
import routes from "./routes";
import "@/retriever.scss";
import Retriever from '@components/Retriever.vue';
import {addDevtools} from "@/vue/devtool";
import {initLogger} from "@/logger";

(async () => {
	initLogger();

	const router = createRouter({
		history: createWebHistory(),
		routes,
	});
	const app = createApp(Retriever);

	app.use(router)
	addDevtools(app);
	await router.isReady();
	app.mount('#vue-app');
})();

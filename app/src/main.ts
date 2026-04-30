/// <reference types="@angular/localize" />

import { bootstrapApplication } from '@angular/platform-browser';
import { App } from './app/app';
import { appConfig } from './app/app.config';
import { saveCurrentPath } from './utils';

saveCurrentPath();

bootstrapApplication(App, appConfig).catch((err) => console.error(err));

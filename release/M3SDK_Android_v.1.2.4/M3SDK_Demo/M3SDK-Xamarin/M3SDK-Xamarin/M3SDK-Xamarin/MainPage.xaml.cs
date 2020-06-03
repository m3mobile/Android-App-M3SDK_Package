using System;
using System.Collections.ObjectModel;
using System.ComponentModel;
using Xamarin.Forms;


namespace M3SDK_Xamarin
{
    // Learn more about making custom code visible in the Xamarin.Forms previewer
    // by visiting https://aka.ms/xamarinforms-previewer
    [DesignTimeVisible(false)]
    public partial class MainPage : ContentPage
    {
        private ObservableCollection<string> _scanned { get; set; }

        public MainPage()
        {
            InitializeComponent();
            _scanned = new ObservableCollection<string>();
            listView_scanned.ItemsSource = _scanned;
        }

        protected override void OnAppearing()
        {
            base.OnAppearing();

            var scan = DependencyService.Get<IM3Scanner>();
            scan.RegisterReceiver();

            MessagingCenter.Subscribe<App, string>(this, "barcode", (sender, arg) =>
            {
                _scanned.Add("Data: " + arg);
            });

            MessagingCenter.Subscribe<App, int>(this, "value", (sender, arg) =>
            {
                edValue.Text = "" + arg;   
            });
        }

        protected override void OnDisappearing()
        {
            var scan = DependencyService.Get<IM3Scanner>();
            MessagingCenter.Unsubscribe<App, string>(this, "barcode");
            scan.UnregisterReceiver();
            base.OnDisappearing();
        }

        private void Button_Clicked_Start(object sender, EventArgs e)
        {
            var scan = DependencyService.Get<IM3Scanner>();

            scan.DecodeStart();            
        }

        private void Button_Clicked_Stop(object sender, EventArgs e)
        {
            var scan = DependencyService.Get<IM3Scanner>();

            scan.DecodeStop();
        }

        private void Button_Clicked_Enable(object sender, EventArgs e)
        {
            var scan = DependencyService.Get<IM3Scanner>();
            scan.SetEnable(true);
        }

        private void Button_Clicked_Disable(object sender, EventArgs e)
        {
            var scan = DependencyService.Get<IM3Scanner>();
            scan.SetEnable(false);
        }

        private void Button_Clicked_GetParam(object sender, EventArgs e)
        {
            int nParam = Int32.Parse(edParam.Text);
            var scan = DependencyService.Get<IM3Scanner>();
            scan.GetScanParam(nParam);
            // return by MessagingCenter
        }

        private void Button_Clicked_SetParam(object sender, EventArgs e)
        {            
            int nParam = Int32.Parse(edParam.Text);
            int nValue = Int32.Parse(edValue.Text);
            var scan = DependencyService.Get<IM3Scanner>();
            scan.SetScanParam(nParam, nValue);
        }

        private void chkKeyDisable_CheckedChanged_KeyDisable(object sender, CheckedChangedEventArgs e)
        {
            var scan = DependencyService.Get<IM3Scanner>();
            scan.SetKeyDisable(chkKeyDisable.IsChecked);
        }
    }
}

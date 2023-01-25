# How to start the ansible playbook

**For the proper functioning, please specify the same username and password for the accounts that will be created in your VirtualBox installation to make ansible variables works. They can be changed after running the playbook.**
**If you know how to configure a Nat Network using VirtualBox and SSH using key authentication, you can skip and go directly to Ansible configuration.**

### Virtual Machines installations

**Don't forget to remove the ISO file after the installation.**

Firstly, you will need to create two virtual machines, one fedora and one debian.
To do this, I advise you to download the ISO images at the following links:
[ISO fedora](https://download.fedoraproject.org/pub/fedora/linux/releases/37/Server/x86_64/iso/Fedora-Server-netinst-x86_64-37-1.7.iso)
[ISO debian](https://cdimage.debian.org/debian-cd/current/amd64/iso-cd/debian-11.6.0-amd64-netinst.iso)

Once the ISOs are downloaded, we can configure our machines in VirtualBox.

#### Fedora Installation

The first step is to create a new machine, we start with the fedora. When you have created your machine with the number of RAM, ... you can go to the configuration of it and go to "Storage" and click on the disk icon on the right then "choose a disk file". Provide the ISO of fedora that you have downloaded.

![VB ISO](/source/images/os7.png)

For the proper functioning of this demonstration, we will use the Virtual Box NAT network.

To do this, we must create a new NAT network by going to "Files" and "Settings" then "Network" as shown in this image :

![VB ISO](/source/images/os2.png)

When we click on the green button on the right a new network is created :

![VB ISO](/source/images/os3.png)

When we double click on it, it is possible to configure the network as you wish :

![VB ISO](/source/images/os4.png)

Now we go back to the configuration of our machine and we can go to "Networks" and choose the type "Nat Network" then select the one we want like this :

![VB ISO](/source/images/os6.png)

Now we can launch our fedora virtual machine and we arrive on this screen :

![VB ISO](/source/images/os8.png)

Choose the first option "Install Fedora 37". Then you can specify the keyboard that suits you :

![VB ISO](/source/images/os9.png)

On the screen that appears, you must click on the logo in the upper right:

![VB ISO](/source/images/os10.png)

Normally you don't have to choose your disk because it is enabled by default. Just click on "Done" at the top left :

![VB ISO](/source/images/os11.png)

For the super user account configuration, we click on it and leave it disabled for security reasons. You can then click on "Done".

![VB ISO](/source/images/os12.png)

![VB ISO](/source/images/osroot.png)

VirtualBox prevents you from seeing the option to create a user account, just press tab and enter which will take you to the following page :

![VB ISO](/source/images/osuser.png)

There you can fill in the name and password you want. Be careful, you must leave the boxes checked for the proper functioning of the playbook.

Now, you can start the installation :

![VB ISO](/source/images/os15.png)

When it is finished, you have to restart the system.

![VB ISO](/source/images/os16.png)
![VB ISO](/source/images/os17.png)

#### Debian Installation

For the debian machine, you have to do the same configuration on VirtualBox, so I invite you to reread the previous parts if necessary. Attention, I advise you to put at least 3GB of RAM for the debian and two CPUs

Once the configuration is done, let's launch the debian virtual machine :

![VB ISO](/source/images/debian1.png)

Here, we will proceed to a normal installation so we choose the 2nd option.

Then, you have to configure the OS language, the country and the keyboard language :

![VB ISO](/source/images/debian2.png)

![VB ISO](/source/images/debian3.png)

![VB ISO](/source/images/debian4.png)

For the name of your machine and the domain, you can leave it by default :

![VB ISO](/source/images/debian5.png)
![VB ISO](/source/images/debian6.png)

After that, you need to configure the password of the super user :

![VB ISO](/source/images/debian7.png)
![VB ISO](/source/images/debian8.png)

Same to create a normal user account :

![VB ISO](/source/images/debian9.png)
![VB ISO](/source/images/debian10.png)
![VB ISO](/source/images/debian11.png)
![VB ISO](/source/images/debian12.png)

For the next four screens you can leave the default values for simplicity :

![VB ISO](/source/images/debian13.png)
![VB ISO](/source/images/debian14.png)
![VB ISO](/source/images/debian15.png)
![VB ISO](/source/images/debian16.png)

It is necessary to accept the writing of the changes :

![VB ISO](/source/images/debian17.png)

We have no other media to analyze so we leave it on "No" :

![VB ISO](/source/images/debian18.png)

For the next four screens you can leave the default values for simplicity :

![VB ISO](/source/images/debian19.png)
![VB ISO](/source/images/debian20.png)
![VB ISO](/source/images/debian21.png)
![VB ISO](/source/images/debian22.png)

Attention, for the selection of the software, it is necessary to uncheck "GNOME", "Desktop" and activate the "SSH" service by pressing space. This gives us this :

![VB ISO](/source/images/debian23.png)

Then, you press enter twice to validate the software and install grub :

![VB ISO](/source/images/debian24.png)

Here, we can take our drive and press enter :

![VB ISO](/source/images/debian25.png)

And press "Continue", that will reboot your virtual machine:

![VB ISO](/source/images/debian26.png)
![VB ISO](/source/images/debian27.png)

#### SSH Configuration

On both virtuals machines, you need to enable the PasswordAuthentication :

(FEDORA)

```bash
sudo nano /etc/ssh/sshd_config
```

(DEBIAN)

```bash
su -
nano /etc/ssh/sshd_config
```

(CTRL+X -> O -> To quit nano)

And change here :

![VB ISO](/source/images/ssh6.png)

Restart your SSH service to update config file :

(FEDORA)

```bash
sudo systemctl restart sshd
```

(DEBIAN)

```bash
systemctl restart sshd
```

Now, on your local machine, you need to create a new key pair with ssh-keygen :

![VB ISO](/source/images/ssh.png)

Then, we configure our Nat Network create on Virtual Box like this :

![VB ISO](/source/images/debian34µ.png)

You just need to change your "IP Guest", to retrieve IP do this on your machine :

```bash
ip a
```

Then, always on your host machine, do this based on your port and username to copy your public key into into virtual machines :

![VB ISO](/source/images/debian35.png)
![VB ISO](/source/images/ssh4.png)

If this success, you can connect to your machine with SSH like this :

![VB ISO](/source/images/debian36.png)

Now, we need to deactivate PasswordAuthentication in SSH configuration file and enable PubKeyAuthentication on both virtuals machines :

![VB ISO](/source/images/debian37.png)

Restart your SSH service to update config file :

(FEDORA)

```bash
sudo systemctl restart sshd
```

(DEBIAN)

```bash
systemctl restart sshd
```

### Ansible Configuration

Firstly, you need to clone the repository on your local machine and install ansible :

```bash
git clone https://gitlab.com/DTM-Henallux/MASI/etudiants/bettignies-edouard/examen-architecture-des-systemes-exploitation.git
```

```bash
python3 -m pip install --user ansible
```

Now, you need to install different collection :
- ansible.posix
- community.crypto
- community.docker
- community.general

To perform that, you need to execute this command :
```bash
ansible-galaxy collection install <name>
```

![Galaxy](/source/images/galaxy.png)


Go to the ansible folder and change the following files at your convinience :

- all.yml --> In groups_vars/all
- debian.yml --> In host_vars/debian
- fedora.yml --> In host_vars/fedora

The only variables that you can't modify are :

- group_sudo
- user_become --> in host_vars/debian only

For the variable user_become in host_vars/fedora and user_ansible, you need to provide the username you create on your virtuals machines.

Now, you need to create two vault files, execute this command in groups_vars/all and roles/docker/vars :

```bash
ansible-vault create vault.yml
```

Then provide the password of your choice.

To use vault, you need to undestand vim and you need to provide this variable in YAML format :
(in groups_vars/all)

```yml
secrets:
  realm: <your realm in upper case Ex -> TEST.LAN>
  password: <your administrator password for AD>
  password_user: <your user AD password>
  domain_name: <your domain in upper case Ex -> TEST>
  bind_dn: <your Administrator distinguished Name on your AD>
  base: <your base LDAP often Ex -> dc=test,dc=lan>
  key_path: <path of your private key used to connect on your VMs>
  key: <your public key used to connect on your VMs>
```

(in roles/docker/vars)

```yml
secrets:
  realm: <your realm in upper case Ex -> TEST.LAN>
  password: <your administrator password for AD>
  password_user: <your user AD password>
  bind_dn: <your Administrator distinguished Name on your AD>
  base: <your base LDAP often Ex -> dc=test,dc=lan>
```

**Don't forget to change ansible_port in 00-inventory.yml**

Now you can launch the playbook like this :

```bash
ansible-playbook playbook.yml -i 00-inventory.yml -K --ask-vault-pass
```

The -K make you become super user in the virtual machine, so you need to provide the good password.
The --ask-vault-pass make you unlock the vault files, so you need to provide the vault password you set before.

If all success you got something like this :

![VB ISO](/source/images/ansible4.png)
